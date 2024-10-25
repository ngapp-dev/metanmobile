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

package com.ngapp.metanmobile.feature.stations.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ngapp.metanmobile.core.data.repository.news.NewsResourceQuery
import com.ngapp.metanmobile.core.data.repository.news.UserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.price.PricesRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.ShareManager
import com.ngapp.metanmobile.feature.stations.detail.navigation.StationDetailNavigation
import com.ngapp.metanmobile.feature.stations.detail.state.StationDetailAction
import com.ngapp.metanmobile.feature.stations.detail.state.StationDetailUiState
import com.ngapp.metanmobile.feature.stations.detail.state.StationDetailUiState.Success
import com.ngapp.metanmobile.feature.stations.detail.state.StationDetailUiState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    userStationsRepository: StationResourcesWithFavoritesRepository,
    fuelPricesRepository: PricesRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
    private val userDataRepository: UserDataRepository,
    private val shareManager: ShareManager,
) : ViewModel() {

    private val stationCode = savedStateHandle.toRoute<StationDetailNavigation>().stationCode

    val uiState: StateFlow<StationDetailUiState> = stationDetailUiState(
        stationCode = stationCode,
        userStationsRepository = userStationsRepository,
        userNewsResourceRepository = userNewsResourceRepository,
        fuelPricesRepository = fuelPricesRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = Loading,
        )

    fun triggerAction(action: StationDetailAction) {
        when (action) {
            is StationDetailAction.UpdateStationFavorite -> onUpdateStationFavorite(action.stationCode, action.favorite)
            is StationDetailAction.ShareStation -> onShareStation(action.station)
        }
    }

    private fun onUpdateStationFavorite(stationCode: String, favorite: Boolean) =
        viewModelScope.launch {
            userDataRepository.setStationResourceFavorite(stationCode, favorite)
        }

    private fun onShareStation(station: UserStationResource?) {
        shareManager.createShareStationIntent(station)
    }
}

private fun stationDetailUiState(
    stationCode: String,
    userStationsRepository: StationResourcesWithFavoritesRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
    fuelPricesRepository: PricesRepository,
): Flow<StationDetailUiState> {

    val stationFlow: Flow<UserStationResource> = userStationsRepository.observeAll(
        query = StationResourceQuery(filterStationCodes = setOf(stationCode))
    ).map { it.first() }

    val fuelPriceFlow = fuelPricesRepository.getFuelPrice()

    val relatedNewsFlow = stationFlow.flatMapLatest { station ->
        userNewsResourceRepository.observeAll(
            query = NewsResourceQuery(filterNewsByStationTitle = station.title)
        )
    }

    return combine(
        stationFlow,
        relatedNewsFlow,
        fuelPriceFlow
    ) { stationDetail, relatedNewsList, fuelPrice ->
        Success(
            stationDetail = stationDetail,
            cngPrice = fuelPrice,
            relatedNewsList = relatedNewsList,
        )
    }
}



