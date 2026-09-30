package com.ngapp.metanmobile.feature.favorites.state

import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig

sealed interface FavoritesUiState {
    data class Success(
        val favoriteStationList: List<UserStationResource>,
        val stationSortingConfig: StationSortingConfig,
    ) : FavoritesUiState

    data object Loading : FavoritesUiState
}
