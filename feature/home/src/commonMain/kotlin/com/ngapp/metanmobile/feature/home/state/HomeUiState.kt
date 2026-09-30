package com.ngapp.metanmobile.feature.home.state

import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource

sealed interface HomeUiState {
    data class Success(
        val pinnedNewsList: List<UserNewsResource>,
        val lastNewsList: List<UserNewsResource>,
        val cngPrice: PriceResource?,
        val nearestStation: UserStationResource?,
        val pinnedFaqList: List<FaqResource>,
        val career: CareerResource?,
    ) : HomeUiState

    data object Loading : HomeUiState
}
