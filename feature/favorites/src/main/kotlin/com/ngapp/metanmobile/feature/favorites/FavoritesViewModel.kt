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

package com.ngapp.metanmobile.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.feature.favorites.state.FavoritesAction
import com.ngapp.metanmobile.feature.favorites.state.FavoritesUiState
import com.ngapp.metanmobile.feature.favorites.state.FavoritesUiState.Loading
import com.ngapp.metanmobile.feature.favorites.state.FavoritesUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    userStationsRepository: StationResourcesWithFavoritesRepository,
    private val locationsRepository: LocationsRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private var _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    private var _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet.asStateFlow()

    private var _stationForDelete = MutableStateFlow<UserStationResource?>(null)
    val stationForDelete = _stationForDelete.asStateFlow()

    val uiState: StateFlow<FavoritesUiState> = favoritesUiState(
        searchQuery = searchQuery,
        userStationsRepository = userStationsRepository,
        userDataRepository = userDataRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = Loading,
        )

    fun triggerAction(action: FavoritesAction) {
        when (action) {
            is FavoritesAction.UpdateLocation -> onUpdateLocation(action.hasPermissions)
            is FavoritesAction.ShowAlertDialog -> onShowAlertDialog(action.showDialog)
            is FavoritesAction.ShowBottomSheet -> onShowBottomSheet(action.showBottomSheet)
            is FavoritesAction.UpdateSearchQuery -> onUpdateSearchQuery(action.input)
            is FavoritesAction.UpdateSortingConfig -> onUpdateSortingConfig(action.stationSortingConfig)
            is FavoritesAction.UpdateStationFavorite ->
                onUpdateStationFavorite(action.stationCode, action.favorite)

            is FavoritesAction.UpdateStationForDelete -> onUpdateStationForDelete(action.station)
        }
    }

    private fun onUpdateStationFavorite(stationCode: String, favorite: Boolean) =
        viewModelScope.launch {
            userDataRepository.setStationResourceFavorite(stationCode, favorite)
        }

    private fun onUpdateLocation(hasPermission: Boolean) = viewModelScope.launch {
        locationsRepository.updateLocation(hasPermission)
    }

    private fun onUpdateSortingConfig(stationSortingConfig: StationSortingConfig) =
        viewModelScope.launch {
            userDataRepository.setStationSortingConfig(stationSortingConfig)
        }

    private fun onUpdateSearchQuery(input: String) {
        _searchQuery.value = input
    }

    private fun onShowAlertDialog(showDialog: Boolean) {
        _showDialog.value = showDialog
    }

    private fun onShowBottomSheet(showBottomSheet: Boolean) {
        _showBottomSheet.value = showBottomSheet
    }

    private fun onUpdateStationForDelete(station: UserStationResource) {
        _stationForDelete.value = station
    }
}

private fun favoritesUiState(
    searchQuery: Flow<String>,
    userStationsRepository: StationResourcesWithFavoritesRepository,
    userDataRepository: UserDataRepository,
): Flow<FavoritesUiState> {
    return searchQuery.flatMapLatest { query ->
        combine(
            userStationsRepository.observeAllFavorites(query = StationResourceQuery(searchQuery = query)),
            userDataRepository.userData,
        ) { stationList, userData ->
            Success(
                favoriteStationList = stationList,
                stationSortingConfig = userData.stationSortingConfig,
            )
        }
    }
}