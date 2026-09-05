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

package com.ngapp.metanmobile.core.data.test.repository.station

import android.util.Log
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.station.OfflineFirstStationsRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.database.dao.station.StationResourceDao
import com.ngapp.metanmobile.core.database.model.station.StationResourceEntity
import com.ngapp.metanmobile.core.model.station.StationType
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for the real [OfflineFirstStationsRepository] implementation. Same approach as
 * [com.ngapp.metanmobile.core.data.test.repository.news.OfflineFirstNewsRepositoryTest]: mock
 * the DAO and network, verify the exact parameters the repository derives from a
 * [StationResourceQuery], and verify the sync reconciliation logic.
 */
class OfflineFirstStationsRepositoryTest {

    private val network = mockk<MetanEcogasNetworkDataSource>()
    private val dao = mockk<StationResourceDao>()
    private val repository = OfflineFirstStationsRepository(network, dao)

    private val noopSynchronizer = object : Synchronizer {}

    @Before
    fun setUp() {
        // SyncUtilities' suspendRunCatching logs failures via android.util.Log.
        mockkStatic(Log::class)
        every { Log.i(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    // region getStationResourcesAsc / getStationResourcesDesc

    @Test
    fun `getStationResourcesAsc maps a default query to the dao's default parameters`() = runTest {
        every {
            dao.getStationResourcesAsc(any(), any(), any(), any(), any(), any())
        } returns flowOf(emptyList())

        repository.getStationResourcesAsc(StationResourceQuery()).first()

        verify {
            dao.getStationResourcesAsc(
                useFilterStationCodes = false,
                filterStationCodes = emptySet(),
                useFilterStationTypes = false,
                filterStationTypes = emptySet(),
                sortingType = "STATION_NAME",
                searchQuery = "",
            )
        }
    }

    @Test
    fun `getStationResourcesAsc passes filterStationCodes through and flips useFilterStationCodes`() = runTest {
        every {
            dao.getStationResourcesAsc(any(), any(), any(), any(), any(), any())
        } returns flowOf(emptyList())

        repository.getStationResourcesAsc(
            StationResourceQuery(filterStationCodes = setOf("a", "b")),
        ).first()

        verify {
            dao.getStationResourcesAsc(
                useFilterStationCodes = true,
                filterStationCodes = setOf("a", "b"),
                useFilterStationTypes = false,
                filterStationTypes = emptySet(),
                sortingType = "STATION_NAME",
                searchQuery = "",
            )
        }
    }

    @Test
    fun `getStationResourcesAsc maps filterStationTypes to their type names and flips useFilterStationTypes`() =
        runTest {
            every {
                dao.getStationResourcesAsc(any(), any(), any(), any(), any(), any())
            } returns flowOf(emptyList())

            repository.getStationResourcesAsc(
                StationResourceQuery(filterStationTypes = setOf(StationType.CNG)),
            ).first()

            verify {
                dao.getStationResourcesAsc(
                    useFilterStationCodes = false,
                    filterStationCodes = emptySet(),
                    useFilterStationTypes = true,
                    filterStationTypes = setOf(StationType.CNG.typeName),
                    sortingType = "STATION_NAME",
                    searchQuery = "",
                )
            }
        }

    @Test
    fun `getStationResourcesAsc maps dao entities to external models, preserving order`() = runTest {
        every {
            dao.getStationResourcesAsc(any(), any(), any(), any(), any(), any())
        } returns flowOf(listOf(entity(code = "a", title = "First"), entity(code = "b", title = "Second")))

        val result = repository.getStationResourcesAsc(StationResourceQuery()).first()

        assertEquals(listOf("First", "Second"), result.map { it.title })
    }

    @Test
    fun `getStationResourcesDesc calls the dao's descending query with the same parameter mapping`() = runTest {
        every {
            dao.getStationResourcesDesc(any(), any(), any(), any(), any(), any())
        } returns flowOf(listOf(entity(code = "a", title = "Only")))

        val result = repository.getStationResourcesDesc(
            StationResourceQuery(filterStationCodes = setOf("a")),
        ).first()

        assertEquals(listOf("Only"), result.map { it.title })
        verify {
            dao.getStationResourcesDesc(
                useFilterStationCodes = true,
                filterStationCodes = setOf("a"),
                useFilterStationTypes = false,
                filterStationTypes = emptySet(),
                sortingType = "STATION_NAME",
                searchQuery = "",
            )
        }
        verify(exactly = 0) { dao.getStationResourcesAsc(any(), any(), any(), any(), any(), any()) }
    }

    // endregion

    // region getStationResource

    @Test
    fun `getStationResource passes the code through and maps the result`() = runTest {
        every { dao.getStationResource("agnks_42") } returns flowOf(entity(code = "agnks_42", title = "The One"))

        val result = repository.getStationResource("agnks_42").first()

        assertEquals("agnks_42", result.code)
        assertEquals("The One", result.title)
    }

    // endregion

    // region syncWith

    @Test
    fun `syncWith upserts fetched stations and deletes codes no longer present, returns true`() = runTest {
        coEvery { network.getStations() } returns listOf(
            networkStation(code = "keep"),
            networkStation(code = "new"),
        )
        coEvery { dao.getAllStationIds() } returns listOf("keep", "stale")
        coEvery { dao.deleteStationResources(any()) } returns Unit
        val upserted = slot<List<StationResourceEntity>>()
        coEvery { dao.upsertStationResources(capture(upserted)) } returns Unit

        val result = repository.syncWith(noopSynchronizer)

        assertTrue(result)
        coVerify { dao.deleteStationResources(setOf("stale")) }
        assertEquals(setOf("keep", "new"), upserted.captured.map { it.code }.toSet())
    }

    @Test
    fun `syncWith returns false and never touches the dao when the network call fails`() = runTest {
        coEvery { network.getStations() } throws RuntimeException("offline")

        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
        coVerify(exactly = 0) { dao.deleteStationResources(any()) }
        coVerify(exactly = 0) { dao.upsertStationResources(any()) }
    }

    @Test
    fun `syncWith returns false, does not throw, when the dao write fails`() = runTest {
        coEvery { network.getStations() } returns listOf(networkStation(code = "1"))
        coEvery { dao.getAllStationIds() } returns emptyList()
        coEvery { dao.deleteStationResources(any()) } returns Unit
        coEvery { dao.upsertStationResources(any()) } throws RuntimeException("Room write failed")

        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
    }

    @Test
    fun `syncWith rethrows cancellation instead of turning it into a failure`() = runTest {
        coEvery { network.getStations() } throws CancellationException("cancelled")

        assertFailsWith<CancellationException> {
            repository.syncWith(noopSynchronizer)
        }
    }

    // endregion

    private fun entity(code: String, title: String = "") = StationResourceEntity(
        id = code,
        code = code,
        previewPicture = "",
        detailPicture = "",
        isActive = 1,
        isOperate = 1,
        type = "",
        address = "",
        region = "",
        phones = "",
        service = "",
        workingTime = "",
        payment = "",
        latitude = "",
        longitude = "",
        busyOnMonday = "",
        busyOnTuesday = "",
        busyOnWednesday = "",
        busyOnThursday = "",
        busyOnFriday = "",
        busyOnSaturday = "",
        busyOnSunday = "",
        googleTag = "",
        googleMapsTag = "",
        yandexTag = "",
        title = title,
        dateCreated = 0L,
        url = "",
    )

    private fun networkStation(code: String) = NetworkStationResource(
        id = code,
        code = code,
        dateCreated = "Wed, 02 Sep 2026 12:00:00 +0000",
    )
}
