package com.ngapp.metanmobile.feature.stationdetail.state

import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource

sealed interface StationDetailUiState {
    data class Success(
        val stationDetail: UserStationResource?,
        val cngPrice: PriceResource?,
        val relatedNewsList: List<UserNewsResource>,
    ) : StationDetailUiState

    data object Loading : StationDetailUiState
}
