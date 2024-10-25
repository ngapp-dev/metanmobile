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

package com.ngapp.metanmobile.core.testing.repository

import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationsRepository
import com.ngapp.metanmobile.core.model.station.StationResource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestStationsRepository : StationsRepository {

    /**
     * The backing hot flow for the list of stations ids for testing.
     */
    private val stationResourcesFlow: MutableSharedFlow<List<StationResource>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     * A test-only API to allow controlling the list of location resources from tests.
     */
    fun sendStationResources(stationResources: List<StationResource>) {
        stationResourcesFlow.tryEmit(stationResources)
    }

    override fun getStationResourcesAsc(query: StationResourceQuery): Flow<List<StationResource>> =
        stationResourcesFlow.map { stationResources ->
            var result = stationResources
            query.filterStationCodes?.let { filterStationCodes ->
                result = result.filter { it.code in filterStationCodes }
            }
            result
        }

    override fun getStationResourcesDesc(query: StationResourceQuery): Flow<List<StationResource>> =
        stationResourcesFlow.map { stationResources ->
            var result = stationResources
            query.filterStationCodes?.let { filterStationCodes ->
                result = result.filter { it.code in filterStationCodes }
            }
            result
        }

    override fun getStationResource(stationCode: String): Flow<StationResource> =
        stationResourcesFlow.map { stationResources ->
            stationResources.first { it.code == stationCode }
        }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = true
}
