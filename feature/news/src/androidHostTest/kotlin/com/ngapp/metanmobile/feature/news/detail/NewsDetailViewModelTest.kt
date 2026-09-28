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

package com.ngapp.metanmobile.feature.news.detail

import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.testing.repository.TestNewsRepository
import com.ngapp.metanmobile.core.testing.repository.TestUserDataRepository
import com.ngapp.metanmobile.core.testing.repository.emptyUserData
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.core.ui.ShareManager
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailAction
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailUiState
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NewsDetailViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val newsRepository = TestNewsRepository()
    private val userDataRepository = TestUserDataRepository().apply { setUserData(emptyUserData) }
    private val shareManager = mockk<ShareManager>(relaxed = true)

    private lateinit var viewModel: NewsDetailViewModel

    // Built here, not in a field initializer: that would run before MainDispatcherRule swaps in
    // the test Main dispatcher, and viewModelScope would silently fall back to a background one.
    @Before
    fun setup() {
        viewModel = NewsDetailViewModel(
            newsRepository = newsRepository,
            userDataRepository = userDataRepository,
            shareManager = shareManager,
        )
    }

    @Test
    fun `uiState is Loading until a news id is set`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        newsRepository.sendNewsResources(listOf(NewsResource.init().copy(id = "1")))

        assertEquals(NewsDetailUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Success with the requested news once the id is set`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val news = NewsResource.init().copy(id = "1", title = "Requested news")
        val otherNews = NewsResource.init().copy(id = "2", title = "A different article")
        newsRepository.sendNewsResources(listOf(news, otherNews))
        viewModel.dispatch(NewsDetailAction.SetNewsId("1"))

        val item = viewModel.uiState.value
        assertIs<NewsDetailUiState.Success>(item)
        assertEquals(news, item.news)
    }

    @Test
    fun `SetNewsId marks the news as viewed`() = runTest {
        viewModel.dispatch(NewsDetailAction.SetNewsId("1"))

        assertEquals(setOf("1"), userDataRepository.userData.first().viewedNewsResources)
    }

    @Test
    fun `ShareNews action shares the news currently shown`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val news = NewsResource.init().copy(id = "1")
        newsRepository.sendNewsResources(listOf(news))
        viewModel.dispatch(NewsDetailAction.SetNewsId("1"))

        viewModel.dispatch(NewsDetailAction.ShareNews)

        verify(exactly = 1) { shareManager.createShareNewsIntent(news) }
    }

    @Test
    fun `ShareNews action does nothing before the news has loaded`() = runTest {
        viewModel.dispatch(NewsDetailAction.ShareNews)

        verify(exactly = 0) { shareManager.createShareNewsIntent(any()) }
    }
}
