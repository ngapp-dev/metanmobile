/*
 * Copyright 2026 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.core.data.test.repository.widget

import app.cash.turbine.test
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.data.repository.price.PricesRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.widget.CompositeWidgetDataRepository
import com.ngapp.metanmobile.core.model.location.LocationResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for [CompositeWidgetDataRepository]: the widgets' snapshot of local data, built
 * from the same repositories the app's screens use and never from the network.
 */
class CompositeWidgetDataRepositoryTest {

    private val stationsRepository = FakeUserStationsRepository()
    private val pricesRepository = FakePricesRepository()
    private val locationsRepository = FakeLocationsRepository()

    private val repository = CompositeWidgetDataRepository(
        stationsRepository = stationsRepository,
        pricesRepository = pricesRepository,
        locationsRepository = locationsRepository,
    )

    @Test
    fun `observeWidgetData combines the price, the location and the stations`() = runTest {
        val price = PriceResource.init().copy(content = "1.16")
        val location = LocationResource(id = 1, time = 0L, latitude = 53.9, longitude = 27.5)
        val station = UserStationResource.init().copy(code = "a", distanceBetween = 2.4)
        pricesRepository.price.value = price
        locationsRepository.location.value = location
        stationsRepository.stations.value = listOf(station)

        val data = repository.observeWidgetData().first()

        assertEquals(price, data.cngPrice)
        assertEquals(location, data.location)
        assertEquals(listOf(station), data.stations)
    }

    @Test
    fun `observeWidgetData reports an unknown location and no price as nulls`() = runTest {
        val data = repository.observeWidgetData().first()

        assertNull(data.cngPrice)
        assertNull(data.location)
        assertEquals(emptyList(), data.stations)
    }

    @Test
    fun `observeWidgetData emits again when local data changes`() = runTest {
        repository.observeWidgetData().test {
            assertNull(awaitItem().cngPrice)

            pricesRepository.price.value = PriceResource.init().copy(content = "1.20")

            assertEquals("1.20", awaitItem().cngPrice?.content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeWidgetData skips updates that change nothing`() = runTest {
        val station = UserStationResource.init().copy(code = "a")
        stationsRepository.stations.value = listOf(station)

        repository.observeWidgetData().test {
            awaitItem()

            // A new list with equal content, as a sync re-writing the same rows produces.
            stationsRepository.stations.value = listOf(station.copy())

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}

/** Tiny hand-written fakes, in keeping with the project's existing testing convention. */
private class FakeUserStationsRepository : StationResourcesWithFavoritesRepository {
    val stations = MutableStateFlow<List<UserStationResource>>(emptyList())

    override fun observeAll(query: StationResourceQuery): Flow<List<UserStationResource>> = stations

    override fun observeAllFavorites(query: StationResourceQuery): Flow<List<UserStationResource>> =
        stations.map { list -> list.filter { it.isFavorite } }
}

private class FakePricesRepository : PricesRepository {
    val price = MutableStateFlow<PriceResource?>(null)

    override fun getFuelPrice(): Flow<PriceResource?> = price

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}

private class FakeLocationsRepository : LocationsRepository {
    val location = MutableStateFlow<LocationResource?>(null)

    override fun getLocationResources(): Flow<List<LocationResource>> = location.map(::listOfNotNull)

    override fun getLocationResource(): Flow<LocationResource?> = location

    override suspend fun updateLocation(locationPermissionGranted: Boolean) = Unit
}
