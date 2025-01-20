/*
 * Copyright 2025 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.feature.stations.detail.newdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.data.repository.news.NewsResourceQuery
import com.ngapp.metanmobile.core.data.repository.news.UserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.price.PricesRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.ShareManager
import com.ngapp.metanmobile.feature.stations.detail.newdetail.state.NewStationDetailAction
import com.ngapp.metanmobile.feature.stations.detail.newdetail.state.NewStationDetailUiState
import com.ngapp.metanmobile.feature.stations.detail.newdetail.state.NewStationDetailUiState.Success
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
class NewStationDetailViewModel @Inject constructor(
    private val userStationsRepository: StationResourcesWithFavoritesRepository,
    private val fuelPricesRepository: PricesRepository,
    private val userNewsResourceRepository: UserNewsResourceRepository,
    private val userDataRepository: UserDataRepository,
    private val shareManager: ShareManager,
) : ViewModel() {

//    private val _stationCode = MutableStateFlow("agnks_vitebsk")
//    val stationCode = _stationCode.asStateFlow()

    val uiState: StateFlow<NewStationDetailUiState> = stationDetailUiState(
//        stationCode = stationCode,
        userDataRepository = userDataRepository,
        userStationsRepository = userStationsRepository,
        userNewsResourceRepository = userNewsResourceRepository,
        fuelPricesRepository = fuelPricesRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = NewStationDetailUiState.Loading,
        )

    fun triggerAction(action: NewStationDetailAction) {
        when (action) {
//            is NewStationDetailAction.LoadStationDetail -> onLoadStationDetail(action.stationCode)
            is NewStationDetailAction.UpdateStationCode -> onUpdateStationCode(action.stationCode)
            is NewStationDetailAction.UpdateStationFavorite ->
                onUpdateStationFavorite(action.stationCode, action.favorite)

            is NewStationDetailAction.ShareStation -> onShareStation(action.station)
        }
    }

    private fun onUpdateStationCode(stationCode: String) {
//        _stationCode.value = stationCode
    }

//    private fun onLoadStationDetail(stationCode: String) = viewModelScope.launch {
//        _uiState.update { it.copy(isLoading = false) }
//        val stationFlow: Flow<UserStationResource> = userStationsRepository.observeAll(
//            query = StationResourceQuery(filterStationCodes = setOf(stationCode))
//        ).map { it.first() }
//        val fuelPriceFlow = fuelPricesRepository.getFuelPrice()
//        val relatedNewsFlow = stationFlow.flatMapLatest { station ->
//            userNewsResourceRepository.observeAll(
//                query = NewsResourceQuery(filterNewsByStationTitle = station.title)
//            )
//        }
//        combine(
//            stationFlow,
//            fuelPriceFlow,
//            relatedNewsFlow,
//            ::Triple
//        ).collectLatest { (stationDetail, fuelPrice, relatedNewsList) ->
//            _uiState.update {
//                it.copy(
//                    stationDetail = stationDetail,
//                    cngPrice = fuelPrice,
//                    relatedNewsList = relatedNewsList
//                )
//            }
//        }
//    }

    private fun onUpdateStationFavorite(stationCode: String, favorite: Boolean) =
        viewModelScope.launch {
            userDataRepository.setStationResourceFavorite(stationCode, favorite)
        }

    private fun onShareStation(station: UserStationResource?) {
        shareManager.createShareStationIntent(station)
    }
}

private fun stationDetailUiState(
//    stationCode: StateFlow<String>,
    userDataRepository: UserDataRepository,
    userStationsRepository: StationResourcesWithFavoritesRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
    fuelPricesRepository: PricesRepository,
): Flow<NewStationDetailUiState> {

    val stationFlow = userDataRepository.userData.flatMapLatest { userData ->
        userStationsRepository.observeAll(
            query = StationResourceQuery(filterStationCodes = setOf(userData.stationDetailCode))
        ).map { it.first() }
    }
//    val stationFlow: Flow<UserStationResource> = userStationsRepository.observeAll(
//        query = StationResourceQuery(filterStationCodes = setOf(stationCode.value))
//    ).map { it.first() }

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