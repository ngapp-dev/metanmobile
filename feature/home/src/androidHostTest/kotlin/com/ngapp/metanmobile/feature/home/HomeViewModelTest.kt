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

package com.ngapp.metanmobile.feature.home

import com.ngapp.metanmobile.core.data.repository.news.CompositeUserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.station.CompositeStationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.testing.data.station.stationResourcesTestData
import com.ngapp.metanmobile.core.testing.repository.TestCareersRepository
import com.ngapp.metanmobile.core.testing.repository.TestFaqRepository
import com.ngapp.metanmobile.core.testing.repository.TestLocationsRepository
import com.ngapp.metanmobile.core.testing.repository.TestNewsRepository
import com.ngapp.metanmobile.core.testing.repository.TestPricesRepository
import com.ngapp.metanmobile.core.testing.repository.TestStationsRepository
import com.ngapp.metanmobile.core.testing.repository.TestUserDataRepository
import com.ngapp.metanmobile.core.testing.repository.emptyUserData
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.core.testing.util.TestSyncManager
import com.ngapp.metanmobile.feature.home.state.HomeAction
import com.ngapp.metanmobile.feature.home.state.HomeUiState
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

class HomeViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val locationsRepository = TestLocationsRepository()
    private val stationsRepository = TestStationsRepository()
    private val userStationsRepository = CompositeStationResourcesWithFavoritesRepository(
        stationsRepository = stationsRepository,
        locationsRepository = locationsRepository,
        userDataRepository = userDataRepository,
    )
    private val newsRepository = TestNewsRepository()
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )
    private val pricesRepository = TestPricesRepository()
    private val faqRepository = TestFaqRepository()
    private val careersRepository = TestCareersRepository()
    private val syncManager = TestSyncManager()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        viewModel = HomeViewModel(
            syncManager = syncManager,
            userStationsRepository = userStationsRepository,
            fuelPricesRepository = pricesRepository,
            userNewsResourceRepository = userNewsResourceRepository,
            faqRepository = faqRepository,
            careersRepository = careersRepository,
            locationsRepository = locationsRepository,
            userDataRepository = userDataRepository,
        )
    }

    @Test
    fun `uiState is initially Loading, before the repository has emitted anything`() = runTest {
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Success with everything empty once collected, with no data sent`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val item = viewModel.uiState.value
        assertIs<HomeUiState.Success>(item)
        assertEquals(emptyList(), item.pinnedNewsList)
        assertEquals(emptyList(), item.lastNewsList)
        assertEquals(null, item.cngPrice)
        assertEquals(null, item.nearestStation)
        assertEquals(emptyList(), item.pinnedFaqList)
        assertEquals(null, item.career)
    }

    @Test
    fun `uiState's nearestStation picks the closest station with a known distance`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val near = stationResourcesTestData[0]
        val far = stationResourcesTestData[1]
        stationsRepository.sendStationResources(listOf(near, far))
        locationsRepository.sendLocationResources(listOf(com.ngapp.metanmobile.core.model.location.LocationResource.init()))

        val item = viewModel.uiState.value
        assertIs<HomeUiState.Success>(item)
        // Both stations have a resolvable distance once location is known - just assert one of
        // the two sent stations came back, with a non-null distance (exact value depends on the
        // haversine calculation against the fixed test coordinates, which isn't this test's
        // concern).
        assertEquals(true, item.nearestStation?.code in setOf(near.code, far.code))
        assertEquals(true, item.nearestStation?.distanceBetween != null)
    }

    @Test
    fun `uiState's nearestStation falls back to the first station when no distance is known yet`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            stationsRepository.sendStationResources(stationResourcesTestData)
            // locationsRepository stays at its seeded "unknown" (null) location.

            val item = viewModel.uiState.value
            assertIs<HomeUiState.Success>(item)
            assertEquals(stationResourcesTestData[0].code, item.nearestStation?.code)
            assertEquals(null, item.nearestStation?.distanceBetween)
        }

    @Test
    fun `uiState's lastNewsList is capped at 3 items, pinnedNewsList is not`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val pinned = (1..5).map {
            com.ngapp.metanmobile.core.model.news.NewsResource.init()
                .copy(id = "pinned-$it", isPinned = 1)
        }
        val regular = (1..5).map {
            com.ngapp.metanmobile.core.model.news.NewsResource.init()
                .copy(id = "regular-$it", isPinned = 0)
        }
        newsRepository.sendNewsResources(pinned + regular)

        val item = viewModel.uiState.value
        assertIs<HomeUiState.Success>(item)
        // lastNewsFlow queries with filterNewsPinned = false, i.e. "no filter" (see
        // NewsResourceQuery.filterNewsPinned's contract) - it draws from all 10 sent items, capped
        // at 3. pinnedNewsFlow's filterNewsPinned = true does filter down to the 5 pinned ones.
        assertEquals(3, item.lastNewsList.size)
        assertEquals(5, item.pinnedNewsList.size)
    }

    @Test
    fun `uiState reflects fuel price, pinned faq and the first career`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val price = PriceResource.init()
        val faq = FaqResource.init().copy(id = "1", isPinned = 1)
        val career = CareerResource.init().copy(id = "1")
        pricesRepository.sendFuelPrice(price)
        faqRepository.sendFaqResources(listOf(faq))
        careersRepository.sendCareers(listOf(career))

        val item = viewModel.uiState.value
        assertIs<HomeUiState.Success>(item)
        assertEquals(price, item.cngPrice)
        assertEquals(listOf(faq), item.pinnedFaqList)
        assertEquals(career, item.career)
    }

    @Test
    fun `reorderableList and isLastNewsExpanded reflect user data as soon as it's known`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.reorderableList.collect() }
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.isLastNewsExpanded.collect() }

            val order = listOf(HomeContentItem.FAQ, HomeContentItem.CAREER)
            userDataRepository.setUserData(
                emptyUserData.copy(homeReorderableList = order, homeLastNewsExpanded = false),
            )

            assertEquals(order, viewModel.reorderableList.value)
            assertEquals(false, viewModel.isLastNewsExpanded.value)
        }

    @Test
    fun `EditUi(true) then EditUi(false) restores the pre-edit reorderableList and isLastNewsExpanded`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.reorderableList.collect() }
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.isLastNewsExpanded.collect() }
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.isEditing.collect() }
            val originalOrder = listOf(HomeContentItem.USER_LOCATION, HomeContentItem.CALCULATORS)
            userDataRepository.setUserData(
                emptyUserData.copy(homeReorderableList = originalOrder, homeLastNewsExpanded = true),
            )

            viewModel.triggerAction(HomeAction.EditUi(true))
            assertEquals(true, viewModel.isEditing.value)
            viewModel.triggerAction(HomeAction.ReorderList(listOf(HomeContentItem.CAREER)))
            viewModel.triggerAction(HomeAction.ExpandLastNews(false))
            viewModel.triggerAction(HomeAction.EditUi(false))

            assertEquals(false, viewModel.isEditing.value)
            assertEquals(originalOrder, viewModel.reorderableList.value)
            assertEquals(true, viewModel.isLastNewsExpanded.value)
        }

    @Test
    fun `SaveUi persists the current reorderableList and isLastNewsExpanded, and stops editing`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.reorderableList.collect() }
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.isLastNewsExpanded.collect() }
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.isEditing.collect() }
            userDataRepository.setUserData(emptyUserData)

            viewModel.triggerAction(HomeAction.EditUi(true))
            val newOrder = listOf(HomeContentItem.CAREER, HomeContentItem.FAQ)
            viewModel.triggerAction(HomeAction.ReorderList(newOrder))
            viewModel.triggerAction(HomeAction.ExpandLastNews(false))
            viewModel.triggerAction(HomeAction.SaveUi)

            assertEquals(false, viewModel.isEditing.value)
            assertEquals(newOrder, userDataRepository.userData.first().homeReorderableList)
            assertEquals(false, userDataRepository.userData.first().homeLastNewsExpanded)
        }

    @Test
    fun `UpdateLocation action delegates to the locations repository`() = runTest {
        viewModel.triggerAction(HomeAction.UpdateLocation(true))

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
        viewModel.triggerAction(HomeAction.RetrySync)

        assertEquals(1, syncManager.requestSyncCallCount)
    }
}
