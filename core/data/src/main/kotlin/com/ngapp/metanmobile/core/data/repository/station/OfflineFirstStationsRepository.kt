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

import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.model.station.asEntity
import com.ngapp.metanmobile.core.data.updateDataSync
import com.ngapp.metanmobile.core.database.dao.station.StationResourceDao
import com.ngapp.metanmobile.core.database.model.station.StationResourceEntity
import com.ngapp.metanmobile.core.database.model.station.asExternalModel
import com.ngapp.metanmobile.core.model.station.StationResource
import com.ngapp.metanmobile.core.network.MetanMobileParserDataSource
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.toSet

class OfflineFirstStationsRepository @Inject constructor(
    private val parser: MetanMobileParserDataSource,
    private val stationResourceDao: StationResourceDao,
) : StationsRepository {

    override fun getStationResourcesAsc(query: StationResourceQuery) =
        stationResourceDao.getStationResourcesAsc(
            useFilterStationCodes = query.filterStationCodes != null,
            filterStationCodes = query.filterStationCodes ?: emptySet(),
            useFilterStationTypes = query.filterStationTypes != null,
            filterStationTypes = query.filterStationTypes?.map { it.typeName }?.toSet() ?: emptySet(),
            sortingType = query.sortingType.name,
            searchQuery = query.searchQuery,
        ).map { it.map(StationResourceEntity::asExternalModel) }

    override fun getStationResourcesDesc(query: StationResourceQuery): Flow<List<StationResource>> =
        stationResourceDao.getStationResourcesDesc(
            useFilterStationCodes = query.filterStationCodes != null,
            filterStationCodes = query.filterStationCodes ?: emptySet(),
            useFilterStationTypes = query.filterStationTypes != null,
            filterStationTypes = query.filterStationTypes?.map { it.typeName }?.toSet() ?: emptySet(),
            sortingType = query.sortingType.name,
            searchQuery = query.searchQuery,
        ).map { it.map(StationResourceEntity::asExternalModel) }

    override fun getStationResource(stationCode: String) =
        stationResourceDao.getStationResource(stationCode)
            .map(StationResourceEntity::asExternalModel)

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        return synchronizer.updateDataSync(
            dataFetcher = { parser.getStations() },
            dataWriter = { networkStationList ->
                val newData = networkStationList.map(NetworkStationResource::asEntity)
                val newIds = newData.map { it.code }.toSet()
                val existingIds = stationResourceDao.getAllStationIds().toSet()
                val idsToDelete = existingIds - newIds
                stationResourceDao.deleteStationResources(idsToDelete)
                stationResourceDao.upsertStationResources(newData)
            }
        )
    }
}