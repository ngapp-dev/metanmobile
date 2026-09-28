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

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ngapp.metanmobile.core.database.MetanMobileDatabase
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource
import com.ngapp.metanmobile.core.network.model.sync.NetworkFeedDelta
import com.ngapp.metanmobile.core.network.model.sync.NetworkSyncFeeds
import com.ngapp.metanmobile.core.network.model.sync.NetworkSyncResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Runs [DataSyncCoordinator] against a real in-memory Room database, so what's checked is what
 * actually ends up on disk — including that a failed sync leaves nothing half-written.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DataSyncCoordinatorTest {

    private lateinit var database: MetanMobileDatabase
    private lateinit var network: FakeNetwork
    private lateinit var coordinator: DataSyncCoordinator

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MetanMobileDatabase::class.java,
        ).allowMainThreadQueries().build()
        network = FakeNetwork()
        coordinator = DataSyncCoordinator(network, database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `first sync asks for everything and stores the returned version`() = runTest {
        network.responses += response(version = 5, stations = listOf(station("s1")), news = listOf(news("n1")))

        assertTrue(coordinator.sync())

        assertEquals(listOf(0L), network.requestedSince)
        assertEquals(listOf("s1"), database.stationResourceDao().getAllStationIds())
        assertEquals(listOf("n1"), database.newsResourceDao().getAllNewsIds())
        assertEquals(5L, database.syncMetaDao().getSyncVersion())
    }

    @Test
    fun `next sync asks only for the delta since the stored version`() = runTest {
        network.responses += response(version = 5, stations = listOf(station("s1")))
        network.responses += response(version = 6, stations = listOf(station("s2")))

        coordinator.sync()
        coordinator.sync()

        assertEquals(listOf(0L, 5L), network.requestedSince)
        assertEquals(setOf("s1", "s2"), database.stationResourceDao().getAllStationIds().toSet())
        assertEquals(6L, database.syncMetaDao().getSyncVersion())
    }

    @Test
    fun `a wiped database goes back to a full sync`() = runTest {
        network.responses += response(version = 5, stations = listOf(station("s1")))
        network.responses += response(version = 5, stations = listOf(station("s1")))
        coordinator.sync()

        // What a destructive migration does to the file.
        database.clearAllTables()
        coordinator.sync()

        assertEquals(listOf(0L, 0L), network.requestedSince)
        assertEquals(listOf("s1"), database.stationResourceDao().getAllStationIds())
    }

    @Test
    fun `deleted news is hidden from lists but still opens by id`() = runTest {
        network.responses += response(version = 5, news = listOf(news("n1"), news("n2")))
        network.responses += response(version = 6, deletedNews = listOf("n2"))

        coordinator.sync()
        coordinator.sync()

        assertEquals(listOf("n1"), newsListIds())
        assertEquals("n2", database.newsResourceDao().getNewsResource("n2").first().id)
    }

    @Test
    fun `news that reappears on the server comes back into the lists`() = runTest {
        network.responses += response(version = 5, news = listOf(news("n1")))
        network.responses += response(version = 6, deletedNews = listOf("n1"))
        network.responses += response(version = 7, news = listOf(news("n1")))

        repeat(3) { coordinator.sync() }

        assertEquals(listOf("n1"), newsListIds())
    }

    @Test
    fun `an item that fails to apply rolls back the whole delta and keeps the old version`() = runTest {
        network.responses += response(version = 5, stations = listOf(station("s1")))
        // News is written before stations; the broken station date throws after it.
        network.responses += response(
            version = 6,
            news = listOf(news("n1")),
            stations = listOf(station("s2", dateCreated = "not a date")),
        )
        network.responses += response(version = 6)

        coordinator.sync()
        val result = coordinator.sync()

        assertFalse(result)
        assertEquals(emptyList(), database.newsResourceDao().getAllNewsIds())
        assertEquals(listOf("s1"), database.stationResourceDao().getAllStationIds())
        assertEquals(5L, database.syncMetaDao().getSyncVersion())

        // And the next attempt retries from the same point instead of skipping the lost delta.
        coordinator.sync()
        assertEquals(listOf(0L, 5L, 5L), network.requestedSince)
    }

    @Test
    fun `a network failure writes nothing`() = runTest {
        network.responses += response(version = 5, stations = listOf(station("s1")))
        coordinator.sync()

        network.failure = IllegalStateException("offline")
        val result = coordinator.sync()

        assertFalse(result)
        assertEquals(5L, database.syncMetaDao().getSyncVersion())
        assertEquals(listOf("s1"), database.stationResourceDao().getAllStationIds())
    }

    @Test
    fun `a failed first sync leaves the database unsynced`() = runTest {
        network.failure = IllegalStateException("offline")

        assertFalse(coordinator.sync())

        assertNull(database.syncMetaDao().getSyncVersion())
    }

    private suspend fun newsListIds(): List<String> =
        database.newsResourceDao().getNewsResourcesDesc(sortingType = "DATE", searchQuery = "").first().map { it.id }

    private fun response(
        version: Long,
        news: List<NetworkNewsResource> = emptyList(),
        deletedNews: List<String> = emptyList(),
        stations: List<NetworkStationResource> = emptyList(),
    ) = NetworkSyncResponse(
        version = version,
        feeds = NetworkSyncFeeds(
            news = NetworkFeedDelta(upserted = news, deleted = deletedNews),
            stations = NetworkFeedDelta(upserted = stations),
        ),
    )

    private fun news(id: String) = NetworkNewsResource(id = id, code = id, title = id, dateCreated = VALID_DATE)

    private fun station(code: String, dateCreated: String = VALID_DATE) =
        NetworkStationResource(id = code, code = code, title = code, dateCreated = dateCreated)

    private class FakeNetwork : MetanEcogasNetworkDataSource {
        val responses = ArrayDeque<NetworkSyncResponse>()
        val requestedSince = mutableListOf<Long>()
        var failure: Exception? = null

        override suspend fun getSync(since: Long): NetworkSyncResponse {
            requestedSince += since
            failure?.let { throw it }
            return responses.removeFirst()
        }

        override suspend fun getStations() = error("not used by the coordinator")
        override suspend fun getFuelPrices() = error("not used by the coordinator")
        override suspend fun getFaqList() = error("not used by the coordinator")
        override suspend fun getContacts() = error("not used by the coordinator")
        override suspend fun getNewsList() = error("not used by the coordinator")
        override suspend fun getCareerList() = error("not used by the coordinator")
    }

    private companion object {
        const val VALID_DATE = "Tue, 02 Jan 2024 15:04:05 +0000"
    }
}
