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

import com.ngapp.metanmobile.core.data.Syncable
import com.ngapp.metanmobile.core.model.station.StationResource
import com.ngapp.metanmobile.core.model.station.StationType
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.model.userdata.StationSortingType
import kotlinx.coroutines.flow.Flow

/**
 * Encapsulation class for query parameters for [StationResource]
 */
data class StationResourceQuery(
    /**
     * Station codes to filter for. Null means any station code will match.
     * A set of specific station codes to include in the query.
     */
    val filterStationCodes: Set<String>? = null,

    /**
     * The sorting type to apply when fetching station resources.
     * It determines the primary criterion for sorting the results.
     * Default is [StationSortingType.STATION_NAME], which sorts stations by their names.
     */
    val sortingType: StationSortingType = StationSortingType.STATION_NAME,

    /**
     * The sorting order to apply to the results.
     * It determines the direction of sorting (ascending or descending).
     * Default is [SortingOrder.DESC], which sorts the results in descending order.
     */
    val sortingOrder: SortingOrder = SortingOrder.DESC,

    /**
     * Station types to filter for. Null means any station type will match.
     * A set of specific station types (e.g., CNG or CLFS) to include in the query.
     */
    val filterStationTypes: Set<StationType>? = null,

    /**
     * A search query to filter station resources by name or other relevant fields.
     * An empty string means no search filter will be applied.
     */
    val searchQuery: String = "",
)

/**
 * Data layer implementation for [StationResource]
 */
interface StationsRepository : Syncable {
    fun getStationResourcesAsc(query: StationResourceQuery = StationResourceQuery()): Flow<List<StationResource>>
    fun getStationResourcesDesc(query: StationResourceQuery = StationResourceQuery()): Flow<List<StationResource>>
    fun getStationResource(stationCode: String): Flow<StationResource>
}