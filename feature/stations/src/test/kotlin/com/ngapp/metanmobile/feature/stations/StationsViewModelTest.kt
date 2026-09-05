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

package com.ngapp.metanmobile.feature.stations

import com.ngapp.metanmobile.core.data.repository.station.CompositeStationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.model.location.LocationResource
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingType
import com.ngapp.metanmobile.core.testing.data.station.stationResourcesTestData
import com.ngapp.metanmobile.core.testing.repository.TestLocationsRepository
import com.ngapp.metanmobile.core.testing.repository.TestStationsRepository
import com.ngapp.metanmobile.core.testing.repository.TestUserDataRepository
import com.ngapp.metanmobile.core.testing.repository.emptyUserData
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.core.testing.util.TestSyncManager
import com.ngapp.metanmobile.feature.stations.state.StationsAction
import com.ngapp.metanmobile.feature.stations.state.StationsUiState
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
import kotlin.test.assertTrue

class StationsViewModelTest {

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
    private val syncManager = TestSyncManager()

    private lateinit var viewModel: StationsViewModel

    @Before
    fun setup() {
        viewModel = StationsViewModel(
            syncManager = syncManager,
            userStationsRepository = userStationsRepository,
            locationsRepository = locationsRepository,
            userDataRepository = userDataRepository,
        )
    }

    @Test
    fun `uiState is initially Loading, before the repository has emitted anything`() = runTest {
        assertEquals(StationsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Success with an empty list once collected, with no stations sent`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val item = viewModel.uiState.value
        assertIs<StationsUiState.Success>(item)
        assertEquals(emptyList(), item.stationList)
        assertEquals(null, item.userLocation)
    }

    @Test
    fun `uiState lists all stations, not just favorites`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        stationRepository.sendStationResources(stationResourcesTestData)

        val item = viewModel.uiState.value
        assertIs<StationsUiState.Success>(item)
        assertEquals(stationResourcesTestData.size, item.stationList.size)
    }

    @Test
    fun `uiState reflects the user's location once it's known`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        stationRepository.sendStationResources(stationResourcesTestData)

        val location = LocationResource.init()
        locationsRepository.sendLocationResources(listOf(location))

        val item = viewModel.uiState.value
        assertIs<StationsUiState.Success>(item)
        assertEquals(location, item.userLocation)
    }

    @Test
    fun `uiState reflects the user's station sorting config`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val sortingConfig = StationSortingConfig(
            sortingType = StationSortingType.STATION_NAME,
            sortingOrder = SortingOrder.ASC,
            activeStationTypes = emptyList(),
        )
        userDataRepository.setUserData(emptyUserData.copy(stationSortingConfig = sortingConfig))

        val item = viewModel.uiState.value
        assertIs<StationsUiState.Success>(item)
        assertEquals(sortingConfig, item.stationSortingConfig)
    }

    @Test
    fun `UpdateSearchQuery action filters the station list by title`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        stationRepository.sendStationResources(stationResourcesTestData)

        viewModel.triggerAction(StationsAction.UpdateSearchQuery("oak"))

        val item = viewModel.uiState.value
        assertIs<StationsUiState.Success>(item)
        assertEquals(listOf("ST002"), item.stationList.map { it.code })
    }

    @Test
    fun `UpdateSearchQuery action updates searchQuery`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.searchQuery.collect() }

        viewModel.triggerAction(StationsAction.UpdateSearchQuery("oak"))

        assertEquals("oak", viewModel.searchQuery.value)
    }

    @Test
    fun `ShowAlertDialog action updates showDialog`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.showDialog.collect() }
        assertEquals(false, viewModel.showDialog.value)

        viewModel.triggerAction(StationsAction.ShowAlertDialog(true))

        assertEquals(true, viewModel.showDialog.value)
    }

    @Test
    fun `UpdateStationCode action updates stationCode`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.stationCode.collect() }
        assertEquals("", viewModel.stationCode.value)

        viewModel.triggerAction(StationsAction.UpdateStationCode("ST002"))

        assertEquals("ST002", viewModel.stationCode.value)
    }

    @Test
    fun `UpdateStationFavorite action marks the station favorite in the user data repository`() =
        runTest {
            userDataRepository.setUserData(emptyUserData)

            viewModel.triggerAction(StationsAction.UpdateStationFavorite("ST001", true))

            assertTrue("ST001" in userDataRepository.userData.first().favoriteStationResources)
        }

    @Test
    fun `UpdateSortingConfig action updates both the repository and uiState`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val newConfig = StationSortingConfig(
            sortingType = StationSortingType.STATION_NAME,
            sortingOrder = SortingOrder.ASC,
            activeStationTypes = emptyList(),
        )
        viewModel.triggerAction(StationsAction.UpdateSortingConfig(newConfig))

        val item = viewModel.uiState.value
        assertIs<StationsUiState.Success>(item)
        assertEquals(newConfig, item.stationSortingConfig)
    }

    @Test
    fun `UpdateLocation action delegates to the locations repository`() = runTest {
        viewModel.triggerAction(StationsAction.UpdateLocation(true))

        assertEquals(listOf(true), locationsRepository.updateLocationCalls)
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
        viewModel.triggerAction(StationsAction.RetrySync)

        assertEquals(1, syncManager.requestSyncCallCount)
    }
}
