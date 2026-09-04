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

package com.ngapp.metanmobile.feature.faq

import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.testing.repository.TestFaqRepository
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.core.testing.util.TestSyncManager
import com.ngapp.metanmobile.feature.faq.state.FaqUiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FaqViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val faqRepository = TestFaqRepository()
    private val syncManager = TestSyncManager()

    private lateinit var viewModel: FaqViewModel

    @Before
    fun setup() {
        viewModel = FaqViewModel(
            faqRepository = faqRepository,
            syncManager = syncManager,
        )
    }

    @Test
    fun `uiState is initially Loading, before the repository has emitted anything`() = runTest {
        assertEquals(FaqUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Success with the faq list once the repository emits`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val faqList = listOf(FaqResource.init())
        faqRepository.sendFaqResources(faqList)

        val item = viewModel.uiState.value
        assertIs<FaqUiState.Success>(item)
        assertEquals(faqList, item.faqList)
    }

    @Test
    fun `uiState is Success with an empty list when the repository has none`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        faqRepository.sendFaqResources(emptyList())

        val item = viewModel.uiState.value
        assertIs<FaqUiState.Success>(item)
        assertEquals(emptyList(), item.faqList)
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
    fun `retrySync delegates to the sync manager`() = runTest {
        viewModel.retrySync()

        assertEquals(1, syncManager.requestSyncCallCount)
    }
}
