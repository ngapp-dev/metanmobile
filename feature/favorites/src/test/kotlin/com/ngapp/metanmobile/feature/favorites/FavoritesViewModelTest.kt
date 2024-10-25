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

package com.ngapp.metanmobile.feature.favorites

import com.ngapp.metanmobile.core.data.repository.station.CompositeStationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.testing.data.station.stationResourcesTestData
import com.ngapp.metanmobile.core.testing.repository.TestLocationsRepository
import com.ngapp.metanmobile.core.testing.repository.TestStationsRepository
import com.ngapp.metanmobile.core.testing.repository.TestUserDataRepository
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.feature.favorites.state.FavoritesUiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class FavoritesViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val locationsRepository = TestLocationsRepository()
    private val stationRepository = TestStationsRepository()
    private val userStationsRepository = CompositeStationResourcesWithFavoritesRepository(
        stationsRepository = stationRepository,
        locationsRepository = locationsRepository,
        userDataRepository = userDataRepository,
    )

    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        viewModel = FavoritesViewModel(
            userStationsRepository = userStationsRepository,
            locationsRepository = locationsRepository,
            userDataRepository = userDataRepository,
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(FavoritesUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun oneFavorite_showsInFeed() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        stationRepository.sendStationResources(stationResourcesTestData)
        userDataRepository.setStationResourceFavorite(stationResourcesTestData[0].code, true)
        val item = viewModel.uiState.value
        assertIs<FavoritesUiState.Success>(item)
        assertEquals(item.favoriteStationList.size, 1)
    }

    @Test
    fun oneFavorite_whenRemoving_removesFromFeed() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        // Set the station resources to be used by this test
        stationRepository.sendStationResources(stationResourcesTestData)
        // Start with the resource saved
        userDataRepository.setStationResourceFavorite(stationResourcesTestData[0].code, true)
        // Use viewModel to remove saved resource
        userDataRepository.setStationResourceFavorite(stationResourcesTestData[0].code, false)
        // Verify list of saved resources is now empty
        val item = viewModel.uiState.value
        assertIs<FavoritesUiState.Success>(item)
        assertEquals(item.favoriteStationList.size, 0)
    }
}
