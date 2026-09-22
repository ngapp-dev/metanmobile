/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ngapp.metanmobile.core.data.sync

import com.ngapp.metanmobile.core.data.model.career.asEntity
import com.ngapp.metanmobile.core.data.model.contact.asEntity
import com.ngapp.metanmobile.core.data.model.faq.asEntity
import com.ngapp.metanmobile.core.data.model.news.asEntity
import com.ngapp.metanmobile.core.data.model.price.asEntity
import com.ngapp.metanmobile.core.data.model.station.asEntity
import com.ngapp.metanmobile.core.database.dao.career.CareerResourceDao
import com.ngapp.metanmobile.core.database.dao.contact.ContactResourceDao
import com.ngapp.metanmobile.core.database.dao.faq.FaqResourceDao
import com.ngapp.metanmobile.core.database.dao.news.NewsResourceDao
import com.ngapp.metanmobile.core.database.dao.price.PriceResourceDao
import com.ngapp.metanmobile.core.database.dao.station.StationResourceDao
import com.ngapp.metanmobile.core.datastore.MetanMobilePreferencesDataSource
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.model.career.NetworkCareerResource
import com.ngapp.metanmobile.core.network.model.contact.NetworkContactResource
import com.ngapp.metanmobile.core.network.model.faq.NetworkFaqResource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import com.ngapp.metanmobile.core.network.model.price.NetworkPriceResource
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * What a sync actually *does* — one `GET /api/sync?since=<version>` call (see the sync spec's
 * Этап 2), then applies the delta straight to each feed's DAO. Pulled out of `sync:work` so it's
 * not duplicated: Android's [com.ngapp.metanmobile.sync.workers.SyncWorker] just delegates to this
 * inside a `CoroutineWorker` (keeping WorkManager's constraints/retry/notification, which is the
 * platform-specific *mechanism*), and iOS's `IosSyncManager` drives the exact same coordinator
 * from a plain coroutine (its own, different mechanism — no WorkManager equivalent on iOS).
 *
 * This replaced the old "one repository, one HTTP call, one `existingIds - newIds` diff" shape
 * (still visible in each `OfflineFirst*Repository.syncWith`, kept only because [Syncable]
 * requires an implementation and their own unit tests exercise it directly — nothing in
 * production calls it anymore). That shape is exactly what caused a whole class of data-loss bugs
 * — a transient empty response looked identical to "everything got deleted" from the client's
 * side, because the client had no way to tell "deleted" from "the truncated RSS window moved on"
 * or "the feed briefly failed" (see BUG-1/BUG-2 in the sync spec). Deletion is now something only
 * the server declares (`feeds.<key>.deleted`); the client never computes it itself again.
 *
 * `deleted` is always empty for now — the worker doesn't have tombstones yet (Этап 3 of the sync
 * spec, needs a full site-listing crawl). So today this is functionally a smaller-payload,
 * one-request replacement for the old seven-repository parallel sync — real incremental deletion
 * starts working the moment the worker starts sending non-empty `deleted` lists, with no client
 * change needed.
 *
 * [GithubUserRepository][com.ngapp.metanmobile.core.data.repository.githubuser.GithubUserRepository]
 * is deliberately NOT part of this coordinator: it's only ever read by the About screen, and
 * unauthenticated GitHub API calls are rate-limited per-IP (60/hour) — mobile carriers commonly
 * NAT many subscribers behind one IP, so this call alone was flipping the whole [sync] result to
 * `false` and sending `SyncWorker` into retry even though every real feed had synced fine. It's
 * synced on demand from `AboutViewModel` instead, only when that screen is actually opened.
 */
class DataSyncCoordinator(
    private val network: MetanEcogasNetworkDataSource,
    private val preferences: MetanMobilePreferencesDataSource,
    private val newsResourceDao: NewsResourceDao,
    private val stationResourceDao: StationResourceDao,
    private val contactResourceDao: ContactResourceDao,
    private val faqResourceDao: FaqResourceDao,
    private val careerResourceDao: CareerResourceDao,
    private val priceResourceDao: PriceResourceDao,
) {
    suspend fun sync(): Boolean = try {
        val since = preferences.getSyncVersion()
        val response = network.getSync(since)
        val feeds = response.feeds

        coroutineScope {
            awaitAll(
                async {
                    newsResourceDao.deleteNewsResources(feeds.news.deleted.toSet())
                    newsResourceDao.upsertNewsResources(feeds.news.upserted.map(NetworkNewsResource::asEntity))
                },
                async {
                    // Локальный PK у станций — code, а не id (см. StationResourceDao). Сервер
                    // сейчас всегда шлёт deleted=[] (тумбстоунов ещё нет), так что расхождение
                    // id/code пока ни на что не влияет — но когда тумбстоуны появятся, их придётся
                    // резолвить в code перед тем, как передавать сюда.
                    stationResourceDao.deleteStationResources(feeds.stations.deleted.toSet())
                    stationResourceDao.upsertStationResources(feeds.stations.upserted.map(NetworkStationResource::asEntity))
                },
                async {
                    contactResourceDao.deleteContactResources(feeds.contacts.deleted)
                    contactResourceDao.upsertContactResources(feeds.contacts.upserted.map(NetworkContactResource::asEntity))
                },
                async {
                    faqResourceDao.deleteFaqResources(feeds.faq.deleted.toSet())
                    faqResourceDao.upsertFaqResources(feeds.faq.upserted.map(NetworkFaqResource::asEntity))
                },
                async {
                    careerResourceDao.deleteCareerResources(feeds.career.deleted)
                    careerResourceDao.upsertCareerResources(feeds.career.upserted.map(NetworkCareerResource::asEntity))
                },
                async {
                    priceResourceDao.deletePriceResources(feeds.prices.deleted)
                    priceResourceDao.upsertPriceResources(feeds.prices.upserted.map(NetworkPriceResource::asEntity))
                },
            )
        }

        preferences.setSyncVersion(response.version)
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // См. SyncUtilities.runSync — тот же принцип: не проглатывать причину молча.
        println("Sync failed: ${e::class.simpleName}: ${e.message}")
        false
    }
}
