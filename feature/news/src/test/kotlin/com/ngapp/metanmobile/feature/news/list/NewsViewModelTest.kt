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

package com.ngapp.metanmobile.feature.news.list

import com.ngapp.metanmobile.core.data.repository.news.CompositeUserNewsResourceRepository
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingType
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.testing.repository.TestNewsRepository
import com.ngapp.metanmobile.core.testing.repository.TestUserDataRepository
import com.ngapp.metanmobile.core.testing.repository.emptyUserData
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.core.testing.util.TestSyncManager
import com.ngapp.metanmobile.feature.news.list.state.NewsAction
import com.ngapp.metanmobile.feature.news.list.state.NewsUiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NewsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val newsRepository = TestNewsRepository()
    private val userDataRepository = TestUserDataRepository()
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )
    private val syncManager = TestSyncManager()

    private lateinit var viewModel: NewsViewModel

    @Before
    fun setup() {
        viewModel = NewsViewModel(
            syncManager = syncManager,
            userNewsResourceRepository = userNewsResourceRepository,
            userDataRepository = userDataRepository,
        )
    }

    @Test
    fun `uiState is initially Loading, before the repository has emitted anything`() = runTest {
        assertEquals(NewsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Success with empty lists once collected, with no news sent`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val item = viewModel.uiState.value
        assertIs<NewsUiState.Success>(item)
        assertEquals(emptyList(), item.newsList)
        assertEquals(emptyList(), item.pinnedNewsList)
    }

    @Test
    fun `pinnedNewsList only contains pinned items, newsList contains everything`() = runTest {
        // Per NewsResourceQuery.filterNewsPinned's contract, filterNewsPinned = false means "no
        // filter, any pinned status matches" — not "unpinned only". So a pinned item legitimately
        // shows up in both lists; newsList is not "the rest".
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val pinned = NewsResource.init().copy(id = "1", isPinned = 1, title = "Pinned")
        val regular = NewsResource.init().copy(id = "2", isPinned = 0, title = "Regular")
        newsRepository.sendNewsResources(listOf(pinned, regular))

        val item = viewModel.uiState.value
        assertIs<NewsUiState.Success>(item)
        assertEquals(listOf("1"), item.pinnedNewsList.map { it.id })
        assertEquals(setOf("1", "2"), item.newsList.map { it.id }.toSet())
    }

    @Test
    fun `uiState's regular news list also contains unpinned items, not just pinned ones`() =
        runTest {
            // Regression guard: TestNewsRepository used to filter to isPinned == 1 regardless of
            // the requested filterNewsPinned value, which would make this list empty.
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            newsRepository.sendNewsResources(
                listOf(NewsResource.init().copy(id = "1", isPinned = 0)),
            )

            val item = viewModel.uiState.value
            assertIs<NewsUiState.Success>(item)
            assertEquals(1, item.newsList.size)
        }

    @Test
    fun `hasBeenViewed reflects the viewed news ids in user data`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        newsRepository.sendNewsResources(listOf(NewsResource.init().copy(id = "1", isPinned = 0)))
        userDataRepository.setNewsResourceViewed("1", true)

        val item = viewModel.uiState.value
        assertIs<NewsUiState.Success>(item)
        assertTrue(item.newsList.single().hasBeenViewed)
    }

    @Test
    fun `uiState reflects the user's news sorting config`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val sortingConfig = NewsSortingConfig(
            sortingType = NewsSortingType.NAME,
            sortingOrder = SortingOrder.ASC,
        )
        userDataRepository.setUserData(emptyUserData.copy(newsSortingConfig = sortingConfig))

        val item = viewModel.uiState.value
        assertIs<NewsUiState.Success>(item)
        assertEquals(sortingConfig, item.newsSortingConfig)
    }

    @Test
    fun `UpdateSearchQuery action filters the regular news list by title, but never the pinned list`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            newsRepository.sendNewsResources(
                listOf(
                    NewsResource.init().copy(id = "1", isPinned = 1, title = "Pinned Oak news"),
                    NewsResource.init().copy(id = "2", isPinned = 0, title = "Oak station opens"),
                    NewsResource.init().copy(id = "3", isPinned = 0, title = "Maple station opens"),
                ),
            )

            viewModel.triggerAction(NewsAction.UpdateSearchQuery("oak"))

            val item = viewModel.uiState.value
            assertIs<NewsUiState.Success>(item)
            assertEquals(setOf("1", "2"), item.newsList.map { it.id }.toSet())
            // The pinned list query never carries searchQuery, so it stays unfiltered.
            assertEquals(listOf("1"), item.pinnedNewsList.map { it.id })
        }

    @Test
    fun `UpdateSearchQuery action updates searchQuery`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.searchQuery.collect() }

        viewModel.triggerAction(NewsAction.UpdateSearchQuery("oak"))

        assertEquals("oak", viewModel.searchQuery.value)
    }

    @Test
    fun `ShowAlertDialog action updates showDialog`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.showDialog.collect() }
        assertEquals(false, viewModel.showDialog.value)

        viewModel.triggerAction(NewsAction.ShowAlertDialog(true))

        assertEquals(true, viewModel.showDialog.value)
    }

    @Test
    fun `UpdateSortingConfig action updates both the repository and uiState`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val newConfig = NewsSortingConfig(
            sortingType = NewsSortingType.NAME,
            sortingOrder = SortingOrder.ASC,
        )
        viewModel.triggerAction(NewsAction.UpdateSortingConfig(newConfig))

        val item = viewModel.uiState.value
        assertIs<NewsUiState.Success>(item)
        assertEquals(newConfig, item.newsSortingConfig)
    }

    @Test
    fun `isSyncing reflects the sync manager once collected`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.isSyncing.collect() }

        syncManager.setSyncing(true)

        assertEquals(true, viewModel.isSyncing.value)
    }

    @Test
    fun `syncFailed reflects the sync manager once collected`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.syncFailed.collect() }

        syncManager.setSyncFailed(true)

        assertEquals(true, viewModel.syncFailed.value)
    }

    @Test
    fun `RetrySync action delegates to the sync manager`() = runTest {
        viewModel.triggerAction(NewsAction.RetrySync)

        assertEquals(1, syncManager.requestSyncCallCount)
    }
}
