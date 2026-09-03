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

package com.ngapp.metanmobile.core.data.test.repository.career

import android.util.Log
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.career.OfflineFirstCareersRepository
import com.ngapp.metanmobile.core.database.dao.career.CareerResourceDao
import com.ngapp.metanmobile.core.database.model.career.CareerResourceEntity
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.model.career.NetworkCareerResource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
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
 * Unit tests for the real [OfflineFirstCareersRepository] implementation.
 */
class OfflineFirstCareersRepositoryTest {

    private val network = mockk<MetanEcogasNetworkDataSource>()
    private val dao = mockk<CareerResourceDao>()
    private val repository = OfflineFirstCareersRepository(network, dao)

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

    @Test
    fun `getCareerList maps dao entities to external models, preserving order`() = runTest {
        every { dao.getCareerResources() } returns
            flowOf(listOf(entity(id = "1", title = "First"), entity(id = "2", title = "Second")))

        val result = repository.getCareerList().first()

        assertEquals(listOf("First", "Second"), result.map { it.title })
    }

    @Test
    fun `syncWith upserts fetched careers and deletes ids no longer present, returns true`() = runTest {
        coEvery { network.getCareerList() } returns listOf(networkCareer(id = "keep"), networkCareer(id = "new"))
        coEvery { dao.getAllCareerIds() } returns listOf("keep", "stale")
        coEvery { dao.deleteCareerResources(any()) } returns Unit
        val upserted = slot<List<CareerResourceEntity>>()
        coEvery { dao.upsertCareerResources(capture(upserted)) } returns Unit

        val result = repository.syncWith(noopSynchronizer)

        assertTrue(result)
        coVerify { dao.deleteCareerResources(listOf("stale")) }
        assertEquals(setOf("keep", "new"), upserted.captured.map { it.id }.toSet())
    }

    @Test
    fun `syncWith returns false and never touches the dao when the network call fails`() = runTest {
        coEvery { network.getCareerList() } throws RuntimeException("offline")

        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
        coVerify(exactly = 0) { dao.upsertCareerResources(any()) }
    }

    @Test
    fun `syncWith returns false, does not throw, when the dao write fails`() = runTest {
        coEvery { network.getCareerList() } returns listOf(networkCareer(id = "1"))
        coEvery { dao.getAllCareerIds() } returns emptyList()
        coEvery { dao.deleteCareerResources(any()) } returns Unit
        coEvery { dao.upsertCareerResources(any()) } throws RuntimeException("Room write failed")

        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
    }

    @Test
    fun `syncWith rethrows cancellation instead of turning it into a failure`() = runTest {
        coEvery { network.getCareerList() } throws CancellationException("cancelled")

        assertFailsWith<CancellationException> {
            repository.syncWith(noopSynchronizer)
        }
    }

    private fun entity(id: String, title: String = "") = CareerResourceEntity(
        id = id,
        code = "",
        previewPicture = "",
        detailPicture = "",
        isActive = 1,
        title = title,
        dateCreated = 0L,
        exp = "",
        place = "",
        description = "",
        requirements = "",
        responsibilities = "",
        number = 1,
    )

    private fun networkCareer(id: String) = NetworkCareerResource(
        id = id,
        dateCreated = "Wed, 02 Sep 2026 12:00:00 +0000",
    )
}
