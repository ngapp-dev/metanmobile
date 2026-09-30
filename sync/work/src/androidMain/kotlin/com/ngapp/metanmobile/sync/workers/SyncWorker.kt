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

package com.ngapp.metanmobile.sync.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.tracing.traceAsync
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.ngapp.metanmobile.core.analytics.AnalyticsHelper
import com.ngapp.metanmobile.core.data.sync.DataSyncCoordinator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Syncs the data layer by delegating to [DataSyncCoordinator] — the *what* of a sync (which
 * repositories, fetched in parallel) is shared with iOS's `IosSyncManager`; this worker is only
 * the Android-specific *how* (WorkManager scheduling/constraints/retry/foreground notification).
 */
internal class SyncWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    private val dataSyncCoordinator: DataSyncCoordinator,
    private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        traceAsync("Sync", 0) {
            analyticsHelper.logSyncStarted()

            val syncedSuccessfully = dataSyncCoordinator.sync()

            analyticsHelper.logSyncFinished(syncedSuccessfully)

            when {
                syncedSuccessfully -> Result.success()
                // Keep retrying transient failures (no connectivity, upstream hiccup) with
                // WorkManager's backoff, but give up after a few attempts instead of retrying
                // forever — a persistent failure (e.g. a broken API contract) should surface as
                // FAILED so the UI can offer a manual retry, not spin silently in the background.
                runAttemptCount < MAX_SYNC_ATTEMPTS -> Result.retry()
                else -> Result.failure()
            }
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val context = applicationContext
        val notificationId = 1
        val channelId = "sync_channel"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sync",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Synchronization in progress"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Synchronization")
            .setContentText("Synchronization is in progress")
            .setSmallIcon(syncNotificationIconRes(context))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        return ForegroundInfo(notificationId, notification)
    }

    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }
}

val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

// The `com.android.kotlin.multiplatform.library` target does not expose a generated R class to
// this module's own androidMain Kotlin sources (unlike the classic com.android.library plugin) -
// the res/drawable/sync_work_notification.png bundled in this module still gets merged into the
// final app's resource table at app-build time, just without a compile-time R.drawable symbol
// here to name it. Looking it up by name at runtime is the same workaround every other KMP
// module in this project relies on for its own androidMain resources (none of them reference
// their own R class either - see core:designsystem, feature:news, feature:favorites).
private fun syncNotificationIconRes(context: Context): Int =
    context.resources.getIdentifier(
        "sync_work_notification",
        "drawable",
        context.packageName,
    ).takeIf { it != 0 } ?: android.R.drawable.stat_notify_sync

private const val MAX_SYNC_ATTEMPTS = 5
