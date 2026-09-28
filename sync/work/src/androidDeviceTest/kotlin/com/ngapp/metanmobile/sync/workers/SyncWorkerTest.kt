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

import android.util.Log
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.ngapp.metanmobile.core.analytics.AnalyticsHelper
import com.ngapp.metanmobile.core.analytics.NoOpAnalyticsHelper
import com.ngapp.metanmobile.core.data.sync.DataSyncCoordinator
import com.ngapp.metanmobile.core.database.MetanMobileDatabase
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import com.ngapp.metanmobile.core.network.model.sync.NetworkFeedDelta
import com.ngapp.metanmobile.core.network.model.sync.NetworkSyncFeeds
import com.ngapp.metanmobile.core.network.model.sync.NetworkSyncResponse
import com.ngapp.metanmobile.sync.di.syncModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.assertEquals

/**
 * Runs the real WorkManager -> [DelegatingWorker] -> Koin -> [SyncWorker] -> [DataSyncCoordinator]
 * chain on a device, with only the network faked and the database kept in memory.
 */
class SyncWorkerTest {

    private val context get() = InstrumentationRegistry.getInstrumentation().context

    private lateinit var database: MetanMobileDatabase

    @Before
    fun setup() {
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        database = Room.inMemoryDatabaseBuilder(context, MetanMobileDatabase::class.java).build()
        // DelegatingWorker looks SyncWorker up in the global Koin graph, which the app would
        // normally have started.
        startKoin {
            androidContext(context)
            modules(
                syncModule(),
                module {
                    single { DataSyncCoordinator(FakeNetwork(), database) }
                    single<AnalyticsHelper> { NoOpAnalyticsHelper() }
                },
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
        database.close()
    }

    @Test
    fun testSyncWork() = runBlocking {
        // Create request
        val request = SyncWorker.startUpSyncWork()

        val workManager = WorkManager.getInstance(context)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!

        // Enqueue and wait for result.
        workManager.enqueue(request).result.get()

        // Get WorkInfo and outputData
        val preRunWorkInfo = workManager.getWorkInfoById(request.id).get()

        // Assert
        assertEquals(WorkInfo.State.ENQUEUED, preRunWorkInfo?.state)

        // Tells the testing framework that the constraints have been met
        testDriver.setAllConstraintsMet(request.id)

        val finishedWorkInfo = withTimeout(10_000) {
            workManager.getWorkInfoByIdFlow(request.id).first { it?.state?.isFinished == true }
        }
        assertEquals(WorkInfo.State.SUCCEEDED, finishedWorkInfo?.state)
        assertEquals(listOf("n1"), database.newsResourceDao().getAllNewsIds())
        assertEquals(SYNC_VERSION, database.syncMetaDao().getSyncVersion())
    }

    private class FakeNetwork : MetanEcogasNetworkDataSource {
        override suspend fun getSync(since: Long) = NetworkSyncResponse(
            version = SYNC_VERSION,
            feeds = NetworkSyncFeeds(
                news = NetworkFeedDelta(
                    upserted = listOf(
                        NetworkNewsResource(
                            id = "n1",
                            code = "n1",
                            title = "n1",
                            dateCreated = "Tue, 02 Jan 2024 15:04:05 +0000",
                        ),
                    ),
                ),
            ),
        )

        override suspend fun getStations() = error("not used by the sync")
        override suspend fun getFuelPrices() = error("not used by the sync")
        override suspend fun getFaqList() = error("not used by the sync")
        override suspend fun getContacts() = error("not used by the sync")
        override suspend fun getNewsList() = error("not used by the sync")
        override suspend fun getCareerList() = error("not used by the sync")
    }

    private companion object {
        const val SYNC_VERSION = 5L
    }
}
