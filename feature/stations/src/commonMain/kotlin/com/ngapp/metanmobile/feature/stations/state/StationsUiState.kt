package com.ngapp.metanmobile.feature.stations.state

import com.ngapp.metanmobile.core.model.location.LocationResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig

sealed interface StationsUiState {
    data class Success(
        val stationList: List<UserStationResource>,
        val userLocation: LocationResource?,
        val stationSortingConfig: StationSortingConfig,
    ) : StationsUiState

    data object Loading : StationsUiState
}
