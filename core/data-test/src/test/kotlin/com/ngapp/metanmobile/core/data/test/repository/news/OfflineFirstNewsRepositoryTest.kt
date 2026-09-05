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

package com.ngapp.metanmobile.core.data.test.repository.news

import android.util.Log
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.news.NewsResourceQuery
import com.ngapp.metanmobile.core.data.repository.news.OfflineFirstNewsRepository
import com.ngapp.metanmobile.core.database.dao.news.NewsResourceDao
import com.ngapp.metanmobile.core.database.model.news.NewsResourceEntity
import com.ngapp.metanmobile.core.model.userdata.NewsSortingType
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
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
 * Unit tests for the real [OfflineFirstNewsRepository] implementation. [NewsResourceDao] and
 * [MetanEcogasNetworkDataSource] are covered with MockK - unlike the locations repository, the
 * interesting behavior here is entirely in how a [NewsResourceQuery] gets translated into DAO
 * call parameters and how a sync reconciles the network response against what's stored, so
 * verifying exact calls is more direct than re-implementing the DAO's SQL filtering as a fake.
 */
class OfflineFirstNewsRepositoryTest {

    private val network = mockk<MetanEcogasNetworkDataSource>()
    private val dao = mockk<NewsResourceDao>()
    private val repository = OfflineFirstNewsRepository(network, dao)

    private val noopSynchronizer = object : Synchronizer {}

    @Before
    fun setUp() {
        // SyncUtilities' suspendRunCatching logs failures via android.util.Log, which isn't
        // mocked/stubbed by default outside Robolectric.
        mockkStatic(Log::class)
        every { Log.i(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    // region getNewsResourcesAsc / getNewsResourcesDesc

    @Test
    fun `getNewsResourcesAsc maps a default query to the dao's default parameters`() = runTest {
        every {
            dao.getNewsResourcesAsc(any(), any(), any(), any(), any(), any(), any())
        } returns flowOf(emptyList())

        repository.getNewsResourcesAsc(NewsResourceQuery()).first()

        verify {
            dao.getNewsResourcesAsc(
                useFilterNewsIds = false,
                useFilterNewsByStationTitle = false,
                filterNewsIds = emptySet(),
                filterNewsPinned = false,
                filterNewsByStationTitle = "",
                sortingType = "DATE",
                searchQuery = "",
            )
        }
    }

    @Test
    fun `getNewsResourcesAsc passes filterNewsIds through and flips useFilterNewsIds`() = runTest {
        every {
            dao.getNewsResourcesAsc(any(), any(), any(), any(), any(), any(), any())
        } returns flowOf(emptyList())

        repository.getNewsResourcesAsc(NewsResourceQuery(filterNewsIds = setOf("a", "b"))).first()

        verify {
            dao.getNewsResourcesAsc(
                useFilterNewsIds = true,
                useFilterNewsByStationTitle = false,
                filterNewsIds = setOf("a", "b"),
                filterNewsPinned = false,
                filterNewsByStationTitle = "",
                sortingType = "DATE",
                searchQuery = "",
            )
        }
    }

    @Test
    fun `getNewsResourcesAsc passes station title, pinned flag, sorting and search query through`() = runTest {
        every {
            dao.getNewsResourcesAsc(any(), any(), any(), any(), any(), any(), any())
        } returns flowOf(emptyList())

        repository.getNewsResourcesAsc(
            NewsResourceQuery(
                filterNewsByStationTitle = "Minsk",
                filterNewsPinned = true,
                sortingType = NewsSortingType.NAME,
                searchQuery = "gas",
            ),
        ).first()

        verify {
            dao.getNewsResourcesAsc(
                useFilterNewsIds = false,
                useFilterNewsByStationTitle = true,
                filterNewsIds = emptySet(),
                filterNewsPinned = true,
                filterNewsByStationTitle = "Minsk",
                sortingType = "NAME",
                searchQuery = "gas",
            )
        }
    }

    @Test
    fun `getNewsResourcesAsc maps dao entities to external models, preserving order`() = runTest {
        every {
            dao.getNewsResourcesAsc(any(), any(), any(), any(), any(), any(), any())
        } returns flowOf(listOf(entity(id = "1", title = "First"), entity(id = "2", title = "Second")))

        val result = repository.getNewsResourcesAsc(NewsResourceQuery()).first()

        assertEquals(listOf("First", "Second"), result.map { it.title })
    }

    @Test
    fun `getNewsResourcesDesc calls the dao's descending query with the same parameter mapping`() = runTest {
        every {
            dao.getNewsResourcesDesc(any(), any(), any(), any(), any(), any(), any())
        } returns flowOf(listOf(entity(id = "1", title = "Only")))

        val result = repository.getNewsResourcesDesc(
            NewsResourceQuery(filterNewsIds = setOf("1"), sortingType = NewsSortingType.NAME),
        ).first()

        assertEquals(listOf("Only"), result.map { it.title })
        verify {
            dao.getNewsResourcesDesc(
                useFilterNewsIds = true,
                useFilterNewsByStationTitle = false,
                filterNewsIds = setOf("1"),
                filterNewsPinned = false,
                filterNewsByStationTitle = "",
                sortingType = "NAME",
                searchQuery = "",
            )
        }
        verify(exactly = 0) { dao.getNewsResourcesAsc(any(), any(), any(), any(), any(), any(), any()) }
    }

    // endregion

    // region getNewsResource

    @Test
    fun `getNewsResource passes the id through and maps the result`() = runTest {
        every { dao.getNewsResource("42") } returns flowOf(entity(id = "42", title = "The One"))

        val result = repository.getNewsResource("42").first()

        assertEquals("42", result.id)
        assertEquals("The One", result.title)
    }

    // endregion

    // region syncWith

    @Test
    fun `syncWith upserts fetched news and deletes ids no longer present, returns true`() = runTest {
        coEvery { network.getNewsList() } returns listOf(
            networkEntity(id = "keep"),
            networkEntity(id = "new"),
        )
        coEvery { dao.getAllNewsIds() } returns listOf("keep", "stale")
        coEvery { dao.deleteNewsResources(any()) } returns Unit
        val upserted = slot<List<NewsResourceEntity>>()
        coEvery { dao.upsertNewsResources(capture(upserted)) } returns Unit

        val result = repository.syncWith(noopSynchronizer)

        assertTrue(result)
        coVerify { dao.deleteNewsResources(setOf("stale")) }
        assertEquals(setOf("keep", "new"), upserted.captured.map { it.id }.toSet())
    }

    @Test
    fun `syncWith returns false and never touches the dao when the network call fails`() = runTest {
        coEvery { network.getNewsList() } throws RuntimeException("offline")

        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
        coVerify(exactly = 0) { dao.deleteNewsResources(any()) }
        coVerify(exactly = 0) { dao.upsertNewsResources(any()) }
    }

    @Test
    fun `syncWith returns false, does not throw, when the dao write fails`() = runTest {
        coEvery { network.getNewsList() } returns listOf(networkEntity(id = "1"))
        coEvery { dao.getAllNewsIds() } returns emptyList()
        coEvery { dao.deleteNewsResources(any()) } returns Unit
        coEvery { dao.upsertNewsResources(any()) } throws RuntimeException("Room write failed")

        // Unlike OfflineFirstLocationsRepository.updateLocation(), syncWith()'s dataWriter runs
        // inside the same suspendRunCatching as the network fetch - a DB failure here is caught,
        // not propagated to the caller.
        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
    }

    @Test
    fun `syncWith rethrows cancellation instead of turning it into a failure`() = runTest {
        coEvery { network.getNewsList() } throws CancellationException("cancelled")

        assertFailsWith<CancellationException> {
            repository.syncWith(noopSynchronizer)
        }
    }

    // endregion

    private fun entity(id: String, title: String = "") = NewsResourceEntity(
        id = id,
        code = "",
        isPinned = 0,
        previewPicture = "",
        detailPicture = "",
        isActive = 1,
        isOperate = 1,
        relatedStation = "",
        title = title,
        dateCreated = 0L,
        description = "",
        content = "",
        url = "",
        isSearchable = 1,
    )

    private fun networkEntity(id: String) = NetworkNewsResource(
        id = id,
        dateCreated = "Wed, 02 Sep 2026 12:00:00 +0000",
    )
}
