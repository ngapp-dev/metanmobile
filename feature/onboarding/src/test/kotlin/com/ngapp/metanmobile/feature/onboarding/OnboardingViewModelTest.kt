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

package com.ngapp.metanmobile.feature.onboarding

import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingType
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingType
import com.ngapp.metanmobile.core.testing.repository.TestUserDataRepository
import com.ngapp.metanmobile.core.testing.repository.emptyUserData
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.feature.onboarding.state.OnboardingAction
import com.ngapp.metanmobile.feature.onboarding.state.OnboardingUiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class OnboardingViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()

    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setup() {
        viewModel = OnboardingViewModel(userDataRepository)
    }

    @Test
    fun `uiState is initially Loading, before the repository has emitted anything`() = runTest {
        assertEquals(OnboardingUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Shown when the user hasn't hidden onboarding yet`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        userDataRepository.setUserData(emptyUserData.copy(shouldHideOnboarding = false))

        assertEquals(OnboardingUiState.Shown, viewModel.uiState.value)
    }

    @Test
    fun `uiState is NotShown once the user has hidden onboarding`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        userDataRepository.setUserData(emptyUserData.copy(shouldHideOnboarding = true))

        assertEquals(OnboardingUiState.NotShown, viewModel.uiState.value)
    }

    @Test
    fun `an active subscriber sees uiState flip from Shown to NotShown once onboarding is hidden`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        userDataRepository.setUserData(emptyUserData.copy(shouldHideOnboarding = false))
        assertEquals(OnboardingUiState.Shown, viewModel.uiState.value)

        userDataRepository.setUserData(emptyUserData.copy(shouldHideOnboarding = true))

        assertEquals(OnboardingUiState.NotShown, viewModel.uiState.value)
    }

    @Test
    fun `DismissOnboarding hides onboarding and resets both sorting configs to their defaults`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        userDataRepository.setUserData(
            emptyUserData.copy(
                shouldHideOnboarding = false,
                stationSortingConfig = StationSortingConfig(
                    sortingType = StationSortingType.STATION_NAME,
                    sortingOrder = SortingOrder.ASC,
                    activeStationTypes = emptyList(),
                ),
                newsSortingConfig = NewsSortingConfig(
                    sortingType = NewsSortingType.NAME,
                    sortingOrder = SortingOrder.ASC,
                ),
            ),
        )

        viewModel.triggerAction(OnboardingAction.DismissOnboarding)

        assertEquals(OnboardingUiState.NotShown, viewModel.uiState.value)
        val userData = userDataRepository.userData.first()
        assertEquals(true, userData.shouldHideOnboarding)
        assertEquals(StationSortingConfig.init(), userData.stationSortingConfig)
        assertEquals(NewsSortingConfig.init(), userData.newsSortingConfig)
    }
}
