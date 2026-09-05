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

package com.ngapp.metanmobile.feature.about

import com.ngapp.metanmobile.core.model.githubuser.GithubUserResource
import com.ngapp.metanmobile.core.testing.repository.TestGithubUserRepository
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.core.testing.util.TestSyncManager
import com.ngapp.metanmobile.feature.about.state.AboutUiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AboutViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val githubUserRepository = TestGithubUserRepository()
    private val syncManager = TestSyncManager()

    private lateinit var viewModel: AboutViewModel

    @Before
    fun setup() {
        viewModel = AboutViewModel(
            githubUserRepository = githubUserRepository,
            syncManager = syncManager,
        )
    }

    @Test
    fun `uiState is initially Loading, before the repository has emitted anything`() = runTest {
        assertEquals(AboutUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Success with the github user once the repository emits`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val githubUser = GithubUserResource.init()
        githubUserRepository.sendGithubUser(githubUser)

        val item = viewModel.uiState.value
        assertIs<AboutUiState.Success>(item)
        assertEquals(githubUser, item.githubUser)
    }

    @Test
    fun `uiState is Success with a null user when the repository has none`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        githubUserRepository.sendGithubUser(null)

        val item = viewModel.uiState.value
        assertIs<AboutUiState.Success>(item)
        assertEquals(null, item.githubUser)
    }

    @Test
    fun `isSyncing is initially false`() = runTest {
        assertEquals(false, viewModel.isSyncing.value)
    }

    @Test
    fun `isSyncing reflects the sync manager once collected`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.isSyncing.collect() }

        syncManager.setSyncing(true)

        assertEquals(true, viewModel.isSyncing.value)
    }
}
