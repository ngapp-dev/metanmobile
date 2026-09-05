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

package com.ngapp.metanmobile.feature.cabinet

import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.feature.cabinet.state.CabinetActions
import com.ngapp.metanmobile.feature.cabinet.state.CabinetUiState
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class CabinetViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CabinetViewModel

    @Before
    fun setup() {
        viewModel = CabinetViewModel()
    }

    @Test
    fun `uiState starts as loading, with no error`() = runTest {
        assertEquals(CabinetUiState(isError = false, isLoading = true), viewModel.uiState.value)
    }

    @Test
    fun `UpdateUiState action replaces isError and isLoading`() = runTest {
        viewModel.triggerAction(
            CabinetActions.UpdateUiState(CabinetUiState(isError = true, isLoading = false)),
        )

        assertEquals(CabinetUiState(isError = true, isLoading = false), viewModel.uiState.value)
    }

    @Test
    fun `UpdateUiState action can be applied repeatedly, reflecting only the latest call`() =
        runTest {
            viewModel.triggerAction(
                CabinetActions.UpdateUiState(CabinetUiState(isError = true, isLoading = true)),
            )
            viewModel.triggerAction(
                CabinetActions.UpdateUiState(CabinetUiState(isError = false, isLoading = false)),
            )

            assertEquals(CabinetUiState(isError = false, isLoading = false), viewModel.uiState.value)
        }
}
