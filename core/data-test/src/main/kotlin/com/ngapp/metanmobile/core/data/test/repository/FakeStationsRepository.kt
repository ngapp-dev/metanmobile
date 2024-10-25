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

package com.ngapp.metanmobile.core.data.test.repository

import com.ngapp.metanmobile.core.common.network.Dispatcher
import com.ngapp.metanmobile.core.common.network.MMDispatchers.IO
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.model.station.asEntity
import com.ngapp.metanmobile.core.data.repository.news.NewsRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationsRepository
import com.ngapp.metanmobile.core.database.model.station.StationResourceEntity
import com.ngapp.metanmobile.core.database.model.station.asExternalModel
import com.ngapp.metanmobile.core.model.station.StationResource
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.model.userdata.StationSortingType
import com.ngapp.metanmobile.core.network.demo.DemoMetanMobileParser
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource
import com.ngapp.metanmobile.core.network.network.MetanMobileParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Fake implementation of the [StationsRepository] that retrieves the station resources from a JSON String.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
internal class FakeStationsRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val parser: MetanMobileParser,
) : StationsRepository {

    override fun getStationResourcesAsc(query: StationResourceQuery): Flow<List<StationResource>> =
        flow {
            val stationsList = parser.getStations(true)
                .map(NetworkStationResource::asEntity)
                .map(StationResourceEntity::asExternalModel)
                .applyQueryFilters(query)
                .applySorting(query.copy(sortingOrder = SortingOrder.ASC))
            emit(stationsList)
        }.flowOn(ioDispatcher)

    override fun getStationResourcesDesc(query: StationResourceQuery): Flow<List<StationResource>> =
        flow {
            val stationsList = parser.getStations(true)
                .map(NetworkStationResource::asEntity)
                .map(StationResourceEntity::asExternalModel)
                .applyQueryFilters(query)
                .applySorting(query.copy(sortingOrder = SortingOrder.DESC))
            emit(stationsList)
        }.flowOn(ioDispatcher)

    override fun getStationResource(stationCode: String): Flow<StationResource> = flow {
        val stationResource = parser.getStation(stationCode, true)
            ?.asEntity()
            ?.asExternalModel()
        emit(stationResource ?: StationResource.init())
    }.flowOn(ioDispatcher)

    override suspend fun syncWith(synchronizer: Synchronizer) = true

    private fun List<StationResource>.applyQueryFilters(query: StationResourceQuery): List<StationResource> {
        return this
            .filter { station ->
                (query.filterStationCodes?.contains(station.code) ?: true) &&
                        (station.title.contains(query.searchQuery, ignoreCase = true))
            }
    }

    private fun List<StationResource>.applySorting(query: StationResourceQuery): List<StationResource> {
        return when (query.sortingType) {
            StationSortingType.STATION_NAME -> {
                if (query.sortingOrder == SortingOrder.ASC) {
                    this.sortedBy { it.title }
                } else {
                    this.sortedByDescending { it.title }
                }
            }

            else -> this
        }
    }
}
