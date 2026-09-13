package com.ngapp.metanmobile.feature.stationdetail.state

import com.ngapp.metanmobile.core.model.station.UserStationResource

sealed interface StationDetailAction {
    data class SetStationCode(val stationCode: String?) : StationDetailAction
    data class UpdateStationFavorite(val stationCode: String, val favorite: Boolean) : StationDetailAction
    data class ShareStation(val station: UserStationResource?) : StationDetailAction
}
