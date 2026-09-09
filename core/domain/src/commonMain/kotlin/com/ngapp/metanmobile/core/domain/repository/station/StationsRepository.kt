package com.ngapp.metanmobile.core.domain.repository.station

import com.ngapp.metanmobile.core.domain.sync.Syncable
import com.ngapp.metanmobile.core.model.station.StationResource
import com.ngapp.metanmobile.core.model.station.StationType
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.model.userdata.StationSortingType
import kotlinx.coroutines.flow.Flow

data class StationResourceQuery(
    val filterStationCodes: Set<String>? = null,
    val sortingType: StationSortingType = StationSortingType.STATION_NAME,
    val sortingOrder: SortingOrder = SortingOrder.DESC,
    val filterStationTypes: Set<StationType>? = null,
    val searchQuery: String = "",
)

interface StationsRepository : Syncable {
    fun getStationResourcesAsc(query: StationResourceQuery = StationResourceQuery()): Flow<List<StationResource>>
    fun getStationResourcesDesc(query: StationResourceQuery = StationResourceQuery()): Flow<List<StationResource>>
    fun getStationResource(stationCode: String): Flow<StationResource>
}

interface StationResourcesWithFavoritesRepository {
    fun observeAll(query: StationResourceQuery): Flow<List<UserStationResource>>
    fun observeAllFavorites(query: StationResourceQuery): Flow<List<UserStationResource>>
}
