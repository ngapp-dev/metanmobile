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

import androidx.lifecycle.SavedStateHandle
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.testing.repository.TestNewsRepository
import com.ngapp.metanmobile.core.testing.repository.TestUserDataRepository
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.core.ui.ShareManager
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailAction
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailUiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Runs on Robolectric (not a plain JVM unit test): [NewsDetailViewModel] decodes its nav argument
 * via `SavedStateHandle.toRoute()`, which internally builds a real `android.os.Bundle` — that
 * throws "not mocked" without a real (or Robolectric-shadowed) Android framework, the same
 * reason `StationResourceDaoTest` needs it for Room. Pinned to sdk = 34 for the same reason as
 * that test: the installed Robolectric version doesn't yet support the project's targetSdk 36.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NewsDetailViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val newsRepository = TestNewsRepository()
    private val userDataRepository = TestUserDataRepository()
    private val shareManager = mockk<ShareManager>(relaxed = true)

    private fun viewModel(newsId: String) = NewsDetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf("newsId" to newsId)),
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
        shareManager = shareManager,
    )

    @Test
    fun `uiState is initially Loading, before the repository has emitted anything`() = runTest {
        assertEquals(NewsDetailUiState.Loading, viewModel("1").uiState.value)
    }

    @Test
    fun `uiState is Success with the requested news once the repository emits`() = runTest {
        val viewModel = viewModel("1")
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val news = NewsResource.init().copy(id = "1", title = "Requested news")
        val otherNews = NewsResource.init().copy(id = "2", title = "A different article")
        newsRepository.sendNewsResources(listOf(news, otherNews))

        val item = viewModel.uiState.value
        assertIs<NewsDetailUiState.Success>(item)
        assertEquals(news, item.news)
    }

    @Test
    fun `ShareNews action delegates to the share manager with the given news`() = runTest {
        val viewModel = viewModel("1")
        val news = NewsResource.init().copy(id = "1")
        every { shareManager.createShareNewsIntent(news) } returns Unit

        viewModel.triggerAction(NewsDetailAction.ShareNews(news))

        verify(exactly = 1) { shareManager.createShareNewsIntent(news) }
    }
}
