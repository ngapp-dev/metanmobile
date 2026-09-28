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
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.ngapp.metanmobile.core.database.MetanMobileDatabase
import com.ngapp.metanmobile.core.database.model.syncmeta.SyncMetaEntity
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.model.career.NetworkCareerResource
import com.ngapp.metanmobile.core.network.model.contact.NetworkContactResource
import com.ngapp.metanmobile.core.network.model.faq.NetworkFaqResource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import com.ngapp.metanmobile.core.network.model.price.NetworkPriceResource
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource
import kotlin.coroutines.cancellation.CancellationException

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
 * Only news ever gets a non-empty `deleted` (tombstones from the worker's nightly full-listing
 * crawl, Этап 3), and those rows are hidden rather than deleted — see `is_in_feed` on
 * `NewsResourceEntity`. The other feeds' deletes are wired up but the server never sends any.
 *
 * The whole delta and the new version are written in one transaction, so a failure halfway (a bad
 * item that fails to map, the process being killed) can't leave the database holding part of a
 * delta under the old version — or, worse, all of it under the new one with rows missing.
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
    private val database: MetanMobileDatabase,
) {
    private val syncMetaDao = database.syncMetaDao()
    private val newsResourceDao = database.newsResourceDao()
    private val stationResourceDao = database.stationResourceDao()
    private val contactResourceDao = database.contactResourceDao()
    private val faqResourceDao = database.faqResourceDao()
    private val careerResourceDao = database.careerResourceDao()
    private val priceResourceDao = database.priceResourceDao()

    suspend fun sync(): Boolean = try {
        // null = this database was never synced (fresh install, or dropped by a schema change).
        val since = syncMetaDao.getSyncVersion() ?: 0L
        val response = network.getSync(since)
        val feeds = response.feeds

        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                newsResourceDao.markNewsResourcesNotInFeed(feeds.news.deleted.toSet())
                newsResourceDao.upsertNewsResources(feeds.news.upserted.map(NetworkNewsResource::asEntity))

                // Локальный PK у станций — code, а не id (см. StationResourceDao). Сервер
                // сейчас всегда шлёт deleted=[] (тумбстоунов ещё нет), так что расхождение
                // id/code пока ни на что не влияет — но когда тумбстоуны появятся, их придётся
                // резолвить в code перед тем, как передавать сюда.
                stationResourceDao.deleteStationResources(feeds.stations.deleted.toSet())
                stationResourceDao.upsertStationResources(feeds.stations.upserted.map(NetworkStationResource::asEntity))

                contactResourceDao.deleteContactResources(feeds.contacts.deleted)
                contactResourceDao.upsertContactResources(feeds.contacts.upserted.map(NetworkContactResource::asEntity))

                faqResourceDao.deleteFaqResources(feeds.faq.deleted.toSet())
                faqResourceDao.upsertFaqResources(feeds.faq.upserted.map(NetworkFaqResource::asEntity))

                careerResourceDao.deleteCareerResources(feeds.career.deleted)
                careerResourceDao.upsertCareerResources(feeds.career.upserted.map(NetworkCareerResource::asEntity))

                priceResourceDao.deletePriceResources(feeds.prices.deleted)
                priceResourceDao.upsertPriceResources(feeds.prices.upserted.map(NetworkPriceResource::asEntity))

                syncMetaDao.upsertSyncMeta(SyncMetaEntity(syncVersion = response.version))
            }
        }
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // См. SyncUtilities.runSync — тот же принцип: не проглатывать причину молча.
        println("Sync failed: ${e::class.simpleName}: ${e.message}")
        false
    }
}
