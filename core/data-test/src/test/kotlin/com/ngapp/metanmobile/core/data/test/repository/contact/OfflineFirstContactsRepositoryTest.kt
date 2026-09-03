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

package com.ngapp.metanmobile.core.data.test.repository.contact

import android.util.Log
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.contact.OfflineFirstContactsRepository
import com.ngapp.metanmobile.core.database.dao.contact.ContactResourceDao
import com.ngapp.metanmobile.core.database.model.contact.ContactResourceEntity
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.model.contact.NetworkContactResource
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for the real [OfflineFirstContactsRepository] implementation. Contacts are a
 * single-row table (`LIMIT 1`); `syncWith` only ever upserts, there is no delete step - tested
 * here as documented current behavior, not fixed.
 */
class OfflineFirstContactsRepositoryTest {

    private val network = mockk<MetanEcogasNetworkDataSource>()
    private val dao = mockk<ContactResourceDao>()
    private val repository = OfflineFirstContactsRepository(network, dao)

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
    fun `getContactResource emits null when the dao has no row`() = runTest {
        every { dao.getContactResource() } returns flowOf(null)

        assertNull(repository.getContactResource().first())
    }

    @Test
    fun `getContactResource maps the dao's row to the external model`() = runTest {
        every { dao.getContactResource() } returns flowOf(entity(content = "hello"))

        val result = repository.getContactResource().first()

        assertEquals("hello", result?.content)
    }

    @Test
    fun `syncWith upserts the fetched contacts, returns true`() = runTest {
        coEvery { network.getContacts() } returns listOf(networkContact(id = 1))
        val upserted = slot<List<ContactResourceEntity>>()
        coEvery { dao.upsertContactResources(capture(upserted)) } returns Unit

        val result = repository.syncWith(noopSynchronizer)

        assertTrue(result)
        assertEquals(listOf(1), upserted.captured.map { it.id })
    }

    @Test
    fun `syncWith returns false and never touches the dao when the network call fails`() = runTest {
        coEvery { network.getContacts() } throws RuntimeException("offline")

        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
        coVerify(exactly = 0) { dao.upsertContactResources(any()) }
    }

    @Test
    fun `syncWith returns false, does not throw, when the dao write fails`() = runTest {
        coEvery { network.getContacts() } returns listOf(networkContact(id = 1))
        coEvery { dao.upsertContactResources(any()) } throws RuntimeException("Room write failed")

        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
    }

    @Test
    fun `syncWith rethrows cancellation instead of turning it into a failure`() = runTest {
        coEvery { network.getContacts() } throws CancellationException("cancelled")

        assertFailsWith<CancellationException> {
            repository.syncWith(noopSynchronizer)
        }
    }

    private fun entity(content: String = "") = ContactResourceEntity(
        id = 1,
        dateCreated = 0L,
        content = content,
    )

    private fun networkContact(id: Int) = NetworkContactResource(
        id = id,
        dateCreated = "Wed, 02 Sep 2026 12:00:00 +0000",
    )
}
