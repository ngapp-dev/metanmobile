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

import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.career.CareersRepository
import com.ngapp.metanmobile.core.data.repository.contact.ContactsRepository
import com.ngapp.metanmobile.core.data.repository.faq.FaqRepository
import com.ngapp.metanmobile.core.data.repository.news.NewsRepository
import com.ngapp.metanmobile.core.data.repository.price.PricesRepository
import com.ngapp.metanmobile.core.data.repository.station.StationsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * What a sync actually *does* — fetch the same repositories in parallel that master's
 * (Android-only, Hilt) `SyncWorker.doWork()` always synced. Pulled out of `sync:work` so it's not
 * duplicated: Android's [com.ngapp.metanmobile.sync.workers.SyncWorker] now just delegates to this
 * inside a `CoroutineWorker` (keeping WorkManager's constraints/retry/notification, which is the
 * platform-specific *mechanism*), and iOS's `IosSyncManager` drives the exact same coordinator
 * from a plain coroutine (its own, different mechanism — no WorkManager equivalent on iOS).
 *
 * [GithubUserRepository][com.ngapp.metanmobile.core.data.repository.githubuser.GithubUserRepository]
 * is deliberately NOT part of this coordinator: it's only ever read by the About screen, and
 * unauthenticated GitHub API calls are rate-limited per-IP (60/hour) — mobile carriers commonly
 * NAT many subscribers behind one IP, so this call alone was flipping the whole [sync] result to
 * `false` and sending `SyncWorker` into retry even though every real feed had synced fine. It's
 * synced on demand from `AboutViewModel` instead, only when that screen is actually opened.
 */
class DataSyncCoordinator(
    private val newsRepository: NewsRepository,
    private val stationsRepository: StationsRepository,
    private val contactsRepository: ContactsRepository,
    private val faqRepository: FaqRepository,
    private val careersRepository: CareersRepository,
    private val pricesRepository: PricesRepository,
) : Synchronizer {
    suspend fun sync(): Boolean = coroutineScope {
        awaitAll(
            async { newsRepository.sync() },
            async { stationsRepository.sync() },
            async { contactsRepository.sync() },
            async { faqRepository.sync() },
            async { careersRepository.sync() },
            async { pricesRepository.sync() },
        ).all { it }
    }
}
