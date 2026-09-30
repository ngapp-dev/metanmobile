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

package com.ngapp.metanmobile.sync.schedulers

import com.ngapp.metanmobile.core.data.sync.DataSyncCoordinator
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import platform.BackgroundTasks.BGAppRefreshTask
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.darwin.dispatch_get_main_queue

private const val BG_SYNC_TASK_IDENTIFIER = "com.ngapp.metanmobile.ios.sync"

// iOS treats this as a minimum — it schedules the actual run opportunistically, based on usage
// patterns/battery/etc., same "no fixed interval, no guarantee" contract as WorkManager's own
// periodic work minimum, just enforced far more aggressively by iOS in practice.
private const val MIN_BACKGROUND_SYNC_INTERVAL_SECONDS = 15.0 * 60.0

/**
 * The real iOS mechanism, not just an in-process approximation: registers
 * `com.ngapp.metanmobile.ios.sync` (declared in Info.plist's `BGTaskSchedulerPermittedIdentifiers`,
 * alongside the `fetch` `UIBackgroundModes` entry) as a [BGAppRefreshTask] with `BGTaskScheduler`,
 * the same OS-level background-execution API real iOS apps use for periodic data refresh — the
 * actual analog of Android's `WorkManager`, not a substitute for it. iOS itself decides when (or
 * whether) to actually invoke it based on the user's app-usage patterns, battery state, etc. —
 * there is no way to force or guarantee an interval, same as WorkManager's own periodic-work
 * minimums are a floor, not a schedule.
 *
 * Must be registered before the end of [platform.UIKit.UIApplication]'s launch — for this
 * SwiftUI-only app (no `AppDelegate`) that means inside `App.init()`, which is exactly where
 * [com.ngapp.metanmobile.composeapp.initSharedKoin] already runs from
 * (`MetanMobileApp.swift`'s `init()`), so [registerBackgroundSync] is called from there right
 * after Koin starts.
 */
@OptIn(ExperimentalForeignApi::class)
fun registerBackgroundSync(dataSyncCoordinator: DataSyncCoordinator, scope: CoroutineScope) {
    BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
        identifier = BG_SYNC_TASK_IDENTIFIER,
        usingQueue = dispatch_get_main_queue(),
    ) { task ->
        @Suppress("UNCHECKED_CAST")
        handleAppRefresh(task as BGAppRefreshTask, dataSyncCoordinator, scope)
    }
    scheduleNextBackgroundSync()
}

@OptIn(ExperimentalForeignApi::class)
private fun scheduleNextBackgroundSync() {
    val request = BGAppRefreshTaskRequest(identifier = BG_SYNC_TASK_IDENTIFIER)
    request.earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(MIN_BACKGROUND_SYNC_INTERVAL_SECONDS)
    try {
        BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)
    } catch (_: Throwable) {
        // Submission legitimately fails in normal conditions (too many pending requests already
        // queued, running in the Simulator without the debugger-simulated launch, the identifier
        // not matching Info.plist, ...) — there is nothing actionable to do about it here; the
        // next real app launch (via requestSync()) or the next successful registration window
        // tries again.
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun handleAppRefresh(
    task: BGAppRefreshTask,
    dataSyncCoordinator: DataSyncCoordinator,
    scope: CoroutineScope,
) {
    // Always queue the next run first — iOS only keeps one pending request per identifier, and if
    // this run gets expired/killed before finishing, there still needs to be a next one queued.
    scheduleNextBackgroundSync()

    val job: Job = scope.launch {
        val success = runCatching { dataSyncCoordinator.sync() }.getOrDefault(false)
        task.setTaskCompletedWithSuccess(success)
    }
    task.expirationHandler = {
        // iOS gives a background task a strict, short time budget (typically ~30s) and calls this
        // if it's about to run out — cancel the in-flight sync instead of letting iOS kill the
        // process mid-write.
        job.cancel()
    }
}
