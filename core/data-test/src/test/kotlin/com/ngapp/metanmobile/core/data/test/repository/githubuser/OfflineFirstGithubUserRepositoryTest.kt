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

package com.ngapp.metanmobile.core.data.test.repository.githubuser

import android.util.Log
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.githubuser.OfflineFirstGithubUserRepository
import com.ngapp.metanmobile.core.database.dao.githubuser.GithubUserResourceDao
import com.ngapp.metanmobile.core.database.model.githubuser.GithubUserResourceEntity
import com.ngapp.metanmobile.core.network.GithubNetworkDataSource
import com.ngapp.metanmobile.core.network.model.githubuser.NetworkGithubUserResource
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
 * Unit tests for the real [OfflineFirstGithubUserRepository] implementation. Uses
 * `updateSingleDataSync` (a single [NetworkGithubUserResource], not a list) unlike every other
 * repository tested so far.
 */
class OfflineFirstGithubUserRepositoryTest {

    private val network = mockk<GithubNetworkDataSource>()
    private val dao = mockk<GithubUserResourceDao>()
    private val repository = OfflineFirstGithubUserRepository(network, dao)

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
    fun `getGithubUser emits null when the dao has no row`() = runTest {
        every { dao.getGithubUserResource() } returns flowOf(null)

        assertNull(repository.getGithubUser().first())
    }

    @Test
    fun `getGithubUser maps the dao's row to the external model`() = runTest {
        every { dao.getGithubUserResource() } returns flowOf(entity(login = "ngapp-dev"))

        val result = repository.getGithubUser().first()

        assertEquals("ngapp-dev", result?.login)
    }

    @Test
    fun `syncWith upserts the fetched user as a single-element list, returns true`() = runTest {
        coEvery { network.getGithubUser() } returns networkUser(login = "ngapp-dev")
        val upserted = slot<List<GithubUserResourceEntity>>()
        coEvery { dao.upsertGithubUserResources(capture(upserted)) } returns Unit

        val result = repository.syncWith(noopSynchronizer)

        assertTrue(result)
        assertEquals(listOf("ngapp-dev"), upserted.captured.map { it.login })
    }

    @Test
    fun `syncWith returns false and never touches the dao when the network call fails`() = runTest {
        coEvery { network.getGithubUser() } throws RuntimeException("offline")

        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
        coVerify(exactly = 0) { dao.upsertGithubUserResources(any()) }
    }

    @Test
    fun `syncWith returns false, does not throw, when the dao write fails`() = runTest {
        coEvery { network.getGithubUser() } returns networkUser(login = "ngapp-dev")
        coEvery { dao.upsertGithubUserResources(any()) } throws RuntimeException("Room write failed")

        val result = repository.syncWith(noopSynchronizer)

        assertFalse(result)
    }

    @Test
    fun `syncWith rethrows cancellation instead of turning it into a failure`() = runTest {
        coEvery { network.getGithubUser() } throws CancellationException("cancelled")

        assertFailsWith<CancellationException> {
            repository.syncWith(noopSynchronizer)
        }
    }

    private fun entity(login: String = "") = GithubUserResourceEntity(
        id = 1,
        login = login,
        avatarUrl = "",
        url = "",
        htmlUrl = "",
        name = "",
        company = "",
        blog = "",
        location = "",
        email = "",
        bio = "",
        twitterUsername = "",
    )

    private fun networkUser(login: String) = NetworkGithubUserResource(id = 1, login = login)
}
