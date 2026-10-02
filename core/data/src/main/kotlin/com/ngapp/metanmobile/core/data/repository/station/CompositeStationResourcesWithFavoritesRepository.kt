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

package com.ngapp.metanmobile.core.data.repository.station

import com.ngapp.metanmobile.core.common.util.distanceInKm
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.station.mapToUserStationResources
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.model.userdata.StationSortingType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Implements a [StationResourcesWithFavoritesRepository] by combining a [StationsRepository] with ¬
 * [LocationsRepository] and [UserDataRepository].
 */
class CompositeStationResourcesWithFavoritesRepository(
    private val stationsRepository: StationsRepository,
    private val locationsRepository: LocationsRepository,
    private val userDataRepository: UserDataRepository,
) : StationResourcesWithFavoritesRepository {

    /**
     * Returns available station resources (joined with user data) matching the given query.
     */
    override fun observeAll(query: StationResourceQuery): Flow<List<UserStationResource>> {
        return userDataRepository.userData.flatMapLatest { userData ->
            val sortingOrderQuery = query.copy(sortingType = userData.stationSortingConfig.sortingType)

            val stationResourcesFlow = when (userData.stationSortingConfig.sortingOrder) {
                SortingOrder.ASC -> stationsRepository.getStationResourcesAsc(sortingOrderQuery)
                SortingOrder.DESC -> stationsRepository.getStationResourcesDesc(sortingOrderQuery)
            }

            val locationFlow = locationsRepository.getLocationResource()

            stationResourcesFlow.flatMapLatest { stationResources ->
                locationFlow.map { location ->
                    stationResources.mapToUserStationResources(userData).map { userStation ->
                        // null location = we don't know the user's position yet — leave
                        // distanceBetween null too, rather than faking a number.
                        val distanceBetween = location?.let {
                            distanceInKm(
                                it.latitude,
                                it.longitude,
                                userStation.latitude.toDouble(),
                                userStation.longitude.toDouble(),
                            )
                        }
                        userStation.copy(distanceBetween = distanceBetween)
                    }.sortedByConfig(sortingOrderQuery.sortingType, userData.stationSortingConfig.sortingOrder)
                }
            }
        }
    }

    /**
     * The database already orders by name; distance depends on the user's position, so it is
     * sorted here. Stations without a known distance go last, keeping their name order.
     */
    private fun List<UserStationResource>.sortedByConfig(
        sortingType: StationSortingType,
        sortingOrder: SortingOrder,
    ): List<UserStationResource> = when (sortingType) {
        StationSortingType.STATION_NAME -> this
        StationSortingType.DISTANCE -> {
            val (known, unknown) = partition { it.distanceBetween != null }
            val byDistance = known.sortedBy { it.distanceBetween }
            val ordered = if (sortingOrder == SortingOrder.DESC) byDistance.reversed() else byDistance
            ordered + unknown
        }
    }

    /**
     * Returns available favorite station resources (joined with user data) matching the given query.
     */
    override fun observeAllFavorites(query: StationResourceQuery): Flow<List<UserStationResource>> =
        userDataRepository.userData.map { it.favoriteStationResources }.distinctUntilChanged()
            .flatMapLatest { favoriteStationResources ->
                when {
                    favoriteStationResources.isEmpty() -> flowOf(emptyList())
                    else -> observeAll(
                        query = query.copy(filterStationCodes = favoriteStationResources),
                    )
                }
            }
}
