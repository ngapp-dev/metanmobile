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
import com.ngapp.metanmobile.core.model.station.StationType
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingType
import com.ngapp.metanmobile.core.testing.data.station.stationResourcesTestData
import com.ngapp.metanmobile.core.testing.repository.TestLocationsRepository
import com.ngapp.metanmobile.core.testing.repository.TestStationsRepository
import com.ngapp.metanmobile.core.testing.repository.TestUserDataRepository
import com.ngapp.metanmobile.core.testing.repository.emptyUserData
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.feature.favorites.state.FavoritesAction
import com.ngapp.metanmobile.feature.favorites.state.FavoritesUiState
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
    fun `uiState is initially Loading, before the repository has emitted anything`() = runTest {
        assertEquals(FavoritesUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Success with an empty list when the user has no favorites`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        stationRepository.sendStationResources(stationResourcesTestData)
        userDataRepository.setUserData(emptyUserData)

        val item = viewModel.uiState.value
        assertIs<FavoritesUiState.Success>(item)
        assertEquals(emptyList(), item.favoriteStationList)
    }

    @Test
    fun `oneFavorite_showsInFeed`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        stationRepository.sendStationResources(stationResourcesTestData)
        userDataRepository.setStationResourceFavorite(stationResourcesTestData[0].code, true)
        val item = viewModel.uiState.value
        assertIs<FavoritesUiState.Success>(item)
        assertEquals(item.favoriteStationList.size, 1)
    }

    @Test
    fun `favorite station carries isFavorite = true and its own data through the mapping`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            stationRepository.sendStationResources(stationResourcesTestData)
            userDataRepository.setStationResourceFavorite(stationResourcesTestData[0].code, true)

            val item = viewModel.uiState.value
            assertIs<FavoritesUiState.Success>(item)
            val favorite = item.favoriteStationList.single()
            assertEquals(stationResourcesTestData[0].code, favorite.code)
            assertEquals(stationResourcesTestData[0].title, favorite.title)
            assertTrue(favorite.isFavorite)
        }

    @Test
    fun `uiState only contains stations whose codes are marked as favorite`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        stationRepository.sendStationResources(stationResourcesTestData)
        userDataRepository.setStationResourceFavorite(stationResourcesTestData[0].code, true)
        userDataRepository.setStationResourceFavorite(stationResourcesTestData[2].code, true)

        val item = viewModel.uiState.value
        assertIs<FavoritesUiState.Success>(item)
        assertEquals(
            setOf(stationResourcesTestData[0].code, stationResourcesTestData[2].code),
            item.favoriteStationList.map { it.code }.toSet(),
        )
    }

    @Test
    fun `oneFavorite_whenRemoving_removesFromFeed`() = runTest {
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

    @Test
    fun `uiState reflects the user's station sorting config`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        stationRepository.sendStationResources(stationResourcesTestData)
        val sortingConfig = StationSortingConfig(
            sortingType = StationSortingType.STATION_NAME,
            sortingOrder = SortingOrder.ASC,
            activeStationTypes = listOf(StationType.CNG),
        )
        userDataRepository.setUserData(emptyUserData.copy(stationSortingConfig = sortingConfig))

        val item = viewModel.uiState.value
        assertIs<FavoritesUiState.Success>(item)
        assertEquals(sortingConfig, item.stationSortingConfig)
    }

    @Test
    fun `UpdateSearchQuery action updates searchQuery`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.searchQuery.collect() }

        viewModel.triggerAction(FavoritesAction.UpdateSearchQuery("oak"))

        assertEquals("oak", viewModel.searchQuery.value)
    }

    @Test
    fun `UpdateSearchQuery action filters the favorite list by station title`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        stationRepository.sendStationResources(stationResourcesTestData)
        userDataRepository.setStationResourceFavorite(stationResourcesTestData[0].code, true) // "Main Station"
        userDataRepository.setStationResourceFavorite(
            stationResourcesTestData[1].code,
            true,
        ) // "Oak Electric Station"

        viewModel.triggerAction(FavoritesAction.UpdateSearchQuery("oak"))

        val item = viewModel.uiState.value
        assertIs<FavoritesUiState.Success>(item)
        assertEquals(listOf(stationResourcesTestData[1].code), item.favoriteStationList.map { it.code })
    }

    @Test
    fun `UpdateSearchQuery action with no match yields an empty Success, not Loading`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        stationRepository.sendStationResources(stationResourcesTestData)
        userDataRepository.setStationResourceFavorite(stationResourcesTestData[0].code, true)

        viewModel.triggerAction(FavoritesAction.UpdateSearchQuery("no such station"))

        val item = viewModel.uiState.value
        assertIs<FavoritesUiState.Success>(item)
        assertEquals(emptyList(), item.favoriteStationList)
    }

    @Test
    fun `ShowAlertDialog action updates showDialog`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.showDialog.collect() }
        assertEquals(false, viewModel.showDialog.value)

        viewModel.triggerAction(FavoritesAction.ShowAlertDialog(true))

        assertEquals(true, viewModel.showDialog.value)
    }

    @Test
    fun `ShowBottomSheet action updates showBottomSheet`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.showBottomSheet.collect() }
        assertEquals(false, viewModel.showBottomSheet.value)

        viewModel.triggerAction(FavoritesAction.ShowBottomSheet(true))

        assertEquals(true, viewModel.showBottomSheet.value)
    }

    @Test
    fun `UpdateStationForDelete action updates stationForDelete`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.stationForDelete.collect() }
        assertEquals(null, viewModel.stationForDelete.value)

        val station = UserStationResource.init()
        viewModel.triggerAction(FavoritesAction.UpdateStationForDelete(station))

        assertEquals(station, viewModel.stationForDelete.value)
    }

    @Test
    fun `UpdateStationCode action updates stationCode`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.stationCode.collect() }
        assertEquals("", viewModel.stationCode.value)

        viewModel.triggerAction(FavoritesAction.UpdateStationCode("ST002"))

        assertEquals("ST002", viewModel.stationCode.value)
    }

    @Test
    fun `UpdateStationFavorite action marks the station favorite in the user data repository`() =
        runTest {
            userDataRepository.setUserData(emptyUserData)

            viewModel.triggerAction(FavoritesAction.UpdateStationFavorite("ST001", true))

            assertTrue("ST001" in userDataRepository.userData.first().favoriteStationResources)
        }

    @Test
    fun `UpdateStationFavorite action unmarks the station favorite in the user data repository`() =
        runTest {
            userDataRepository.setUserData(emptyUserData.copy(favoriteStationResources = setOf("ST001")))

            viewModel.triggerAction(FavoritesAction.UpdateStationFavorite("ST001", false))

            assertTrue("ST001" !in userDataRepository.userData.first().favoriteStationResources)
        }

    @Test
    fun `UpdateSortingConfig action updates both the repository and uiState`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        stationRepository.sendStationResources(stationResourcesTestData)
        userDataRepository.setUserData(emptyUserData)

        val newConfig = StationSortingConfig(
            sortingType = StationSortingType.STATION_NAME,
            sortingOrder = SortingOrder.ASC,
            activeStationTypes = listOf(StationType.CNG, StationType.CLFS),
        )
        viewModel.triggerAction(FavoritesAction.UpdateSortingConfig(newConfig))

        val item = viewModel.uiState.value
        assertIs<FavoritesUiState.Success>(item)
        assertEquals(newConfig, item.stationSortingConfig)
        assertEquals(newConfig, userDataRepository.userData.first().stationSortingConfig)
    }

    @Test
    fun `UpdateLocation action delegates to the locations repository`() = runTest {
        viewModel.triggerAction(FavoritesAction.UpdateLocation(true))

        assertEquals(listOf(true), locationsRepository.updateLocationCalls)
    }
}
