package com.ngapp.metanmobile.feature.favorites.state

import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig

sealed interface FavoritesAction {
    data class UpdateLocation(val hasPermissions: Boolean) : FavoritesAction
    data class ShowAlertDialog(val showDialog: Boolean) : FavoritesAction
    data class ShowBottomSheet(val showBottomSheet: Boolean) : FavoritesAction
    data class UpdateSearchQuery(val input: String) : FavoritesAction
    data class UpdateSortingConfig(val stationSortingConfig: StationSortingConfig) : FavoritesAction
    data class UpdateStationFavorite(val stationCode: String, val favorite: Boolean) : FavoritesAction
    data class UpdateStationForDelete(val station: UserStationResource) : FavoritesAction
    data class UpdateStationCode(val stationCode: String) : FavoritesAction
}
