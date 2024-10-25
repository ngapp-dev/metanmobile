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
import com.ngapp.metanmobile.core.model.location.LocationResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.station.mapToUserStationResources
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * Implements a [StationResourcesWithFavoritesRepository] by combining a [StationsRepository] with ¬
 * [LocationsRepository] and [UserDataRepository].
 */
class CompositeStationResourcesWithFavoritesRepository @Inject constructor(
    private val stationsRepository: StationsRepository,
    private val locationsRepository: LocationsRepository,
    private val userDataRepository: UserDataRepository,
) : StationResourcesWithFavoritesRepository {

    /**
     * Returns available station resources (joined with user data) matching the given query.
     */
    override fun observeAll(query: StationResourceQuery): Flow<List<UserStationResource>> {
        return userDataRepository.userData.flatMapLatest { userData ->
            val updatedQuery = query.copy(
                sortingType = userData.stationSortingConfig.sortingType,
                filterStationTypes = userData.stationSortingConfig.activeStationTypes.toSet(),
            )

            val stationResourcesFlow = when (userData.stationSortingConfig.sortingOrder) {
                SortingOrder.ASC -> stationsRepository.getStationResourcesAsc(updatedQuery)
                SortingOrder.DESC -> stationsRepository.getStationResourcesDesc(updatedQuery)
            }

            stationResourcesFlow.flatMapLatest { stationResources ->
                locationsRepository.getLocationResource()
                    .onStart { emit(LocationResource.init()) }
                    .filterNotNull()
                    .map { location ->
                        val userStationResources =
                            stationResources.mapToUserStationResources(userData)
                        userStationResources.map { userStation ->
                            val distanceBetween = distanceInKm(
                                location.latitude,
                                location.longitude,
                                userStation.latitude.toDouble(),
                                userStation.longitude.toDouble(),
                            )
                            userStation.copy(distanceBetween = distanceBetween)
                        }
                    }
            }
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