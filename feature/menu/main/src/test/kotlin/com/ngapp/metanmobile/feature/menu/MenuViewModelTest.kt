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

package com.ngapp.metanmobile.feature.menu

import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.testing.repository.TestUserDataRepository
import com.ngapp.metanmobile.core.testing.repository.emptyUserData
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.feature.menu.state.SettingsAction
import com.ngapp.metanmobile.feature.menu.state.SettingsUiState
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

class MenuViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()

    private lateinit var viewModel: MenuViewModel

    @Before
    fun setup() {
        viewModel = MenuViewModel(userDataRepository)
    }

    @Test
    fun `settingsUiState is initially Loading, before the repository has emitted anything`() =
        runTest {
            assertEquals(SettingsUiState.Loading, viewModel.settingsUiState.value)
        }

    @Test
    fun `settingsUiState reflects the user's dark theme config once the repository emits`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) {
                viewModel.settingsUiState.collect()
            }

            userDataRepository.setUserData(emptyUserData.copy(darkThemeConfig = DarkThemeConfig.DARK))

            val item = viewModel.settingsUiState.value
            assertIs<SettingsUiState.Success>(item)
            assertEquals(DarkThemeConfig.DARK, item.darkThemeConfig)
        }

    @Test
    fun `UpdateDarkThemeConfig action updates both the repository and uiState`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.settingsUiState.collect()
        }
        userDataRepository.setUserData(
            emptyUserData.copy(darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM),
        )

        viewModel.triggerAction(SettingsAction.UpdateDarkThemeConfig(DarkThemeConfig.LIGHT))

        val item = viewModel.settingsUiState.value
        assertIs<SettingsUiState.Success>(item)
        assertEquals(DarkThemeConfig.LIGHT, item.darkThemeConfig)
        assertEquals(DarkThemeConfig.LIGHT, userDataRepository.userData.first().darkThemeConfig)
    }
}
