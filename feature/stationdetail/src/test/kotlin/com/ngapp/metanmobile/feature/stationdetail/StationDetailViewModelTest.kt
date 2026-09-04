/*
 * Copyright 2025 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.feature.stationdetail

import com.ngapp.metanmobile.core.data.repository.news.CompositeUserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.station.CompositeStationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.testing.data.station.stationResourcesTestData
import com.ngapp.metanmobile.core.testing.repository.TestLocationsRepository
import com.ngapp.metanmobile.core.testing.repository.TestNewsRepository
import com.ngapp.metanmobile.core.testing.repository.TestPricesRepository
import com.ngapp.metanmobile.core.testing.repository.TestStationsRepository
import com.ngapp.metanmobile.core.testing.repository.TestUserDataRepository
import com.ngapp.metanmobile.core.testing.repository.emptyUserData
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.core.ui.ShareManager
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailAction
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailUiState
import io.mockk.every
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
import kotlin.test.assertTrue

class StationDetailViewModelTest {

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
    private val newsRepository = TestNewsRepository()
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )
    private val pricesRepository = TestPricesRepository()
    private val shareManager = mockk<ShareManager>(relaxed = true)

    private lateinit var viewModel: StationDetailViewModel

    @Before
    fun setup() {
        viewModel = StationDetailViewModel(
            userStationsRepository = userStationsRepository,
            fuelPricesRepository = pricesRepository,
            userNewsResourceRepository = userNewsResourceRepository,
            userDataRepository = userDataRepository,
            shareManager = shareManager,
        )
    }

    @Test
    fun `uiState is Loading before a station code is set`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        assertEquals(StationDetailUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Success with the requested station once a code is set`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        stationRepository.sendStationResources(stationResourcesTestData)

        viewModel.triggerAction(StationDetailAction.SetStationCode("ST002"))

        val item = viewModel.uiState.value
        assertIs<StationDetailUiState.Success>(item)
        assertEquals("ST002", item.stationDetail?.code)
    }

    @Test
    fun `uiState's station is null when no station matches the requested code`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        stationRepository.sendStationResources(stationResourcesTestData)

        viewModel.triggerAction(StationDetailAction.SetStationCode("does-not-exist"))

        val item = viewModel.uiState.value
        assertIs<StationDetailUiState.Success>(item)
        assertEquals(null, item.stationDetail)
    }

    @Test
    fun `SetStationCode action with null resets to Loading`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        stationRepository.sendStationResources(stationResourcesTestData)
        viewModel.triggerAction(StationDetailAction.SetStationCode("ST002"))
        assertIs<StationDetailUiState.Success>(viewModel.uiState.value)

        viewModel.triggerAction(StationDetailAction.SetStationCode(null))

        assertEquals(StationDetailUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState carries the current fuel price`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        stationRepository.sendStationResources(stationResourcesTestData)
        viewModel.triggerAction(StationDetailAction.SetStationCode("ST002"))

        val price = PriceResource.init()
        pricesRepository.sendFuelPrice(price)

        val item = viewModel.uiState.value
        assertIs<StationDetailUiState.Success>(item)
        assertEquals(price, item.cngPrice)
    }

    @Test
    fun `uiState only lists news related to the requested station's title`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        stationRepository.sendStationResources(stationResourcesTestData)
        viewModel.triggerAction(StationDetailAction.SetStationCode("ST002")) // title "Oak Electric Station"

        newsRepository.sendNewsResources(
            listOf(
                NewsResource.init().copy(id = "1", relatedStation = "Oak Electric Station"),
                NewsResource.init().copy(id = "2", relatedStation = "Main Station"),
            ),
        )

        val item = viewModel.uiState.value
        assertIs<StationDetailUiState.Success>(item)
        assertEquals(listOf("1"), item.relatedNewsList.map { it.id })
    }

    @Test
    fun `UpdateStationFavorite action marks the station favorite in the user data repository`() =
        runTest {
            userDataRepository.setUserData(emptyUserData)

            viewModel.triggerAction(StationDetailAction.UpdateStationFavorite("ST001", true))

            assertTrue("ST001" in userDataRepository.userData.first().favoriteStationResources)
        }

    @Test
    fun `UpdateStationFavorite action unmarks the station favorite in the user data repository`() =
        runTest {
            userDataRepository.setUserData(emptyUserData.copy(favoriteStationResources = setOf("ST001")))

            viewModel.triggerAction(StationDetailAction.UpdateStationFavorite("ST001", false))

            assertTrue("ST001" !in userDataRepository.userData.first().favoriteStationResources)
        }

    @Test
    fun `ShareStation action delegates to the share manager with the given station`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        stationRepository.sendStationResources(stationResourcesTestData)
        viewModel.triggerAction(StationDetailAction.SetStationCode("ST002"))
        val station = (viewModel.uiState.value as StationDetailUiState.Success).stationDetail
        every { shareManager.createShareStationIntent(station) } returns Unit

        viewModel.triggerAction(StationDetailAction.ShareStation(station))

        verify(exactly = 1) { shareManager.createShareStationIntent(station) }
    }
}
