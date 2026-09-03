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

package com.ngapp.metanmobile.core.data.test.repository.faq

import android.util.Log
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.faq.FaqResourceQuery
import com.ngapp.metanmobile.core.data.repository.faq.OfflineFirstFaqRepository
import com.ngapp.metanmobile.core.database.dao.faq.FaqResourceDao
import com.ngapp.metanmobile.core.database.model.faq.FaqResourceEntity
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.model.faq.NetworkFaqResource
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
 * Unit tests for the real [OfflineFirstFaqRepository] implementation.
 */
class OfflineFirstFaqRepositoryTest {

    private val network = mockk<MetanEcogasNetworkDataSource>()
    private val dao = mockk<FaqResourceDao>()
    private val repository = OfflineFirstFaqRepository(network, dao)

    private val noopSynchronizer = object : Synchronizer {}

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.i(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    // region getFaqList

    @Test
    fun `getFaqList passes filterFaqListPinned through, other dao params left at their defaults`() = runTest {
        every { dao.getFaqResources(filterFaqListPinned = false) } returns flowOf(emptyList())

        repository.getFaqList(FaqResourceQuery(filterFaqListPinned = false)).first()

        verify { dao.getFaqResources(filterFaqListPinned = false) }
    }

    @Test
    fun `getFaqList passes filterFaqListPinned=true through`() = runTest {
        every { dao.getFaqResources(filterFaqListPinned = true) } returns flowOf(emptyList())

        repository.getFaqList(FaqResourceQuery(filterFaqListPinned = true)).first()

        verify { dao.getFaqResources(filterFaqListPinned = true) }
    }

    @Test
    fun `getFaqList maps dao entities to external models, preserving order`() = runTest {
        every { dao.getFaqResources(filterFaqListPinned = false) } returns
            flowOf(listOf(entity(id = "1", title = "First"), entity(id = "2", title = "Second")))

        val result = repository.getFaqList(FaqResourceQuery()).first()

        assertEquals(listOf("First", "Second"), result.map { it.title })
    }

    // endregion

    // region syncWith

    @Test
    fun `syncWith upserts fetched faqs and deletes ids no longer present, returns true`() = runTest {
        coEvery { network.getFaqList() } returns listOf(networkFaq(id = "keep"), networkFaq(id = "new"))
        coEvery { dao.getAllFaqIds() } returns listOf("keep", "stale")
        coEvery { dao.deleteFaqResources(any()) } returns Unit
        val upserted = slot<List<FaqResourceEntity>>()
        coEvery { dao.upsertFaqResources(capture(upserted)) } returns Unit

        val result = repository.syncWith(noopSynchronizer)

        assertTrue(result)
        coVerify { dao.deleteFaqResources(setOf("stale")) }
        assertEquals(setOf("keep", "new"), upserted.captured.map { it.id }.toSet())
    }

    @Test
    fun `syncWith returns false and never touches the dao when the network call fails`() = runTest {
        coEvery { network.getFaqList() } throws RuntimeException("offline")

        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
        coVerify(exactly = 0) { dao.deleteFaqResources(any()) }
        coVerify(exactly = 0) { dao.upsertFaqResources(any()) }
    }

    @Test
    fun `syncWith returns false, does not throw, when the dao write fails`() = runTest {
        coEvery { network.getFaqList() } returns listOf(networkFaq(id = "1"))
        coEvery { dao.getAllFaqIds() } returns emptyList()
        coEvery { dao.deleteFaqResources(any()) } returns Unit
        coEvery { dao.upsertFaqResources(any()) } throws RuntimeException("Room write failed")

        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
    }

    @Test
    fun `syncWith rethrows cancellation instead of turning it into a failure`() = runTest {
        coEvery { network.getFaqList() } throws CancellationException("cancelled")

        assertFailsWith<CancellationException> {
            repository.syncWith(noopSynchronizer)
        }
    }

    // endregion

    private fun entity(id: String, title: String = "") = FaqResourceEntity(
        id = id,
        code = "",
        isPinned = 0,
        title = title,
        dateCreated = 0L,
        content = "",
    )

    private fun networkFaq(id: String) = NetworkFaqResource(
        id = id,
        dateCreated = "Wed, 02 Sep 2026 12:00:00 +0000",
    )
}
