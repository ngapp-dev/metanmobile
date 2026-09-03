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

package com.ngapp.metanmobile.core.data.test.repository.station

import android.location.Location
import app.cash.turbine.test
import com.ngapp.metanmobile.core.common.util.distanceInKm
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.data.repository.station.CompositeStationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationsRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.location.LocationResource
import com.ngapp.metanmobile.core.model.station.StationResource
import com.ngapp.metanmobile.core.model.station.StationType
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingType
import com.ngapp.metanmobile.core.model.userdata.UserData
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the real [CompositeStationResourcesWithFavoritesRepository] implementation.
 * [StationsRepository] and [LocationsRepository] are covered with small hand-written fakes
 * (project convention, and precise control over reactive emissions is needed for the Turbine
 * tests below); [UserDataRepository] is covered with MockK since only its `userData` property
 * is ever read here, out of a dozen unrelated methods.
 */
class CompositeStationResourcesWithFavoritesRepositoryTest {

    private val stationsRepository = FakeStationsRepository()
    private val locationsRepository = FakeLocationsRepository()
    private val userDataFlow = MutableStateFlow(userData())
    private val userDataRepository = mockk<UserDataRepository> {
        every { userData } returns userDataFlow
    }

    private val repository = CompositeStationResourcesWithFavoritesRepository(
        stationsRepository = stationsRepository,
        locationsRepository = locationsRepository,
        userDataRepository = userDataRepository,
    )

    // region observeAll - sorting order routing

    @Test
    fun `observeAll calls getStationResourcesDesc when the user's sorting order is DESC`() = runTest {
        userDataFlow.value = userData(sortingOrder = SortingOrder.DESC)
        stationsRepository.emit(listOf(station("a")))

        repository.observeAll(StationResourceQuery()).first()

        assertEquals(1, stationsRepository.descQueries.size)
        assertTrue(stationsRepository.ascQueries.isEmpty())
    }

    @Test
    fun `observeAll calls getStationResourcesAsc when the user's sorting order is ASC`() = runTest {
        userDataFlow.value = userData(sortingOrder = SortingOrder.ASC)
        stationsRepository.emit(listOf(station("a")))

        repository.observeAll(StationResourceQuery()).first()

        assertEquals(1, stationsRepository.ascQueries.size)
        assertTrue(stationsRepository.descQueries.isEmpty())
    }

    @Test
    fun `observeAll overrides the query's sortingType with the user's configured one`() = runTest {
        userDataFlow.value = userData(sortingOrder = SortingOrder.DESC, sortingType = StationSortingType.STATION_NAME)
        stationsRepository.emit(emptyList())

        repository.observeAll(StationResourceQuery()).first()

        assertEquals(StationSortingType.STATION_NAME, stationsRepository.descQueries.single().sortingType)
    }

    // endregion

    // region observeAll - distance & favorites

    @Test
    fun `observeAll leaves distanceBetween null when the location isn't known yet`() = runTest {
        stationsRepository.emit(listOf(station("a", lat = "10", lon = "10")))
        locationsRepository.emit(null)

        val result = repository.observeAll(StationResourceQuery()).first()

        assertEquals(listOf(null), result.map { it.distanceBetween })
    }

    @Test
    fun `observeAll computes distanceBetween from the known location`() = runTest {
        stationsRepository.emit(listOf(station("a", lat = "10.0", lon = "20.0")))
        locationsRepository.emit(location(lat = 11.0, lon = 21.0))

        val result = repository.observeAll(StationResourceQuery()).first()

        val expected = distanceInKm(11.0, 21.0, 10.0, 20.0)
        assertEquals(expected, result.single().distanceBetween)
    }

    @Test
    fun `observeAll marks stations present in the user's favorites`() = runTest {
        userDataFlow.value = userData(favorites = setOf("fav"))
        stationsRepository.emit(listOf(station("fav"), station("other")))

        val result = repository.observeAll(StationResourceQuery()).first()

        assertEquals(setOf("fav"), result.filter { it.isFavorite }.map { it.code }.toSet())
    }

    // endregion

    // region observeAll - reactivity

    @Test
    fun `an active subscriber sees distanceBetween update once the location arrives`() = runTest {
        stationsRepository.emit(listOf(station("a", lat = "10.0", lon = "20.0")))
        locationsRepository.emit(null)

        repository.observeAll(StationResourceQuery()).test {
            assertEquals(listOf(null), awaitItem().map { it.distanceBetween })

            locationsRepository.emit(location(lat = 10.0, lon = 20.0))

            val updated = awaitItem()
            assertEquals(1, updated.size)
            assertTrue(updated.single().distanceBetween != null)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `an active subscriber sees results switch queries when the user's sorting order changes`() = runTest {
        userDataFlow.value = userData(sortingOrder = SortingOrder.DESC)
        stationsRepository.emit(listOf(station("a")))

        repository.observeAll(StationResourceQuery()).test {
            awaitItem()
            assertEquals(1, stationsRepository.descQueries.size)

            userDataFlow.value = userData(sortingOrder = SortingOrder.ASC)

            awaitItem()
            assertEquals(1, stationsRepository.ascQueries.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    // region observeAllFavorites

    @Test
    fun `observeAllFavorites emits an empty list without querying stations when there are no favorites`() =
        runTest {
            userDataFlow.value = userData(favorites = emptySet())

            val result = repository.observeAllFavorites(StationResourceQuery()).first()

            assertTrue(result.isEmpty())
            assertTrue(stationsRepository.ascQueries.isEmpty())
            assertTrue(stationsRepository.descQueries.isEmpty())
        }

    @Test
    fun `observeAllFavorites filters by the user's favorite station codes`() = runTest {
        userDataFlow.value = userData(favorites = setOf("fav"), sortingOrder = SortingOrder.DESC)
        stationsRepository.emit(listOf(station("fav"), station("other")))

        val result = repository.observeAllFavorites(StationResourceQuery()).first()

        assertEquals(listOf("fav"), result.map { it.code })
        assertEquals(setOf("fav"), stationsRepository.descQueries.single().filterStationCodes)
    }

    @Test
    fun `an active subscriber on observeAllFavorites sees a newly favorited station appear`() = runTest {
        userDataFlow.value = userData(favorites = emptySet())
        stationsRepository.emit(listOf(station("a")))

        repository.observeAllFavorites(StationResourceQuery()).test {
            assertEquals(emptyList(), awaitItem())

            userDataFlow.value = userData(favorites = setOf("a"))

            val updated = awaitItem()
            assertEquals(listOf("a"), updated.map { it.code })
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    private fun station(code: String, lat: String = "53.0", lon: String = "27.0") =
        StationResource.init().copy(code = code, latitude = lat, longitude = lon)

    private fun location(lat: Double, lon: Double) =
        LocationResource(id = 1, time = 0L, latitude = lat, longitude = lon)

    private fun userData(
        sortingOrder: SortingOrder = SortingOrder.DESC,
        sortingType: StationSortingType = StationSortingType.STATION_NAME,
        favorites: Set<String> = emptySet(),
    ) = UserData(
        favoriteStationResources = favorites,
        viewedNewsResources = emptySet(),
        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
        shouldHideOnboarding = false,
        newsSortingConfig = NewsSortingConfig.init(),
        stationSortingConfig = StationSortingConfig(
            sortingType = sortingType,
            sortingOrder = sortingOrder,
            activeStationTypes = listOf(StationType.CNG),
        ),
        totalUsageTime = 0L,
        isReviewShown = false,
        homeReorderableList = emptyList(),
        homeLastNewsExpanded = true,
    )
}

/** Tiny hand-written fake, in keeping with the project's existing testing convention. */
private class FakeStationsRepository : StationsRepository {

    private val state = MutableStateFlow<List<StationResource>>(emptyList())

    val ascQueries = mutableListOf<StationResourceQuery>()
    val descQueries = mutableListOf<StationResourceQuery>()

    fun emit(stations: List<StationResource>) {
        state.value = stations
    }

    override fun getStationResourcesAsc(query: StationResourceQuery): Flow<List<StationResource>> {
        ascQueries += query
        return applyFilter(query)
    }

    override fun getStationResourcesDesc(query: StationResourceQuery): Flow<List<StationResource>> {
        descQueries += query
        return applyFilter(query)
    }

    override fun getStationResource(stationCode: String): Flow<StationResource> =
        state.map { it.first { s -> s.code == stationCode } }

    override suspend fun syncWith(synchronizer: Synchronizer) = true

    private fun applyFilter(query: StationResourceQuery): Flow<List<StationResource>> =
        state.map { list ->
            query.filterStationCodes?.let { codes -> list.filter { it.code in codes } } ?: list
        }
}

/** Tiny hand-written fake, in keeping with the project's existing testing convention. */
private class FakeLocationsRepository : LocationsRepository {

    private val state = MutableStateFlow<LocationResource?>(null)

    fun emit(location: LocationResource?) {
        state.value = location
    }

    override fun getLocationResources(): Flow<List<LocationResource>> = state.map { listOfNotNull(it) }

    override fun getLocationResource(): Flow<LocationResource?> = state

    override suspend fun getLocationData(): Location? = null

    override suspend fun updateLocation(locationPermissionGranted: Boolean) {}
}
