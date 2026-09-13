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

import com.ngapp.metanmobile.core.domain.sync.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val MAX_SYNC_ATTEMPTS = 5
private const val RETRY_DELAY_MILLIS = 5_000L

/**
 * iOS has no WorkManager equivalent — there is no durable, system-scheduled background job queue
 * reachable from pure Kotlin/Native without native (Swift-side) BackgroundTasks wiring. Master's
 * own actual behavior (and this app's Android side, see [com.ngapp.metanmobile.sync.workers.
 * SyncWorker]) is itself just "sync once at app startup, retrying transient failures with
 * backoff" — not true periodic background sync — so this reproduces exactly that using a plain
 * coroutine instead of WorkManager: same [DataSyncCoordinator] (same repositories, same "sync all
 * 7 in parallel" logic), same retry-until-success-or-give-up policy, different mechanism.
 */
class IosSyncManager(
    private val dataSyncCoordinator: DataSyncCoordinator,
) : SyncManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _isSyncing = MutableStateFlow(false)
    private val _syncFailed = MutableStateFlow(false)

    override val isSyncing = _isSyncing.asStateFlow()
    override val syncFailed = _syncFailed.asStateFlow()

    override fun requestSync() {
        // Mirrors WorkManager's enqueueUniqueWork(..., ExistingWorkPolicy.KEEP): don't stack a
        // second sync run on top of one already in flight.
        if (_isSyncing.value) return
        scope.launch {
            _isSyncing.value = true
            _syncFailed.value = false
            var succeeded = false
            var attempt = 0
            while (!succeeded && attempt < MAX_SYNC_ATTEMPTS) {
                succeeded = dataSyncCoordinator.sync()
                attempt++
                if (!succeeded && attempt < MAX_SYNC_ATTEMPTS) delay(RETRY_DELAY_MILLIS)
            }
            _isSyncing.value = false
            _syncFailed.value = !succeeded
        }
    }
}
