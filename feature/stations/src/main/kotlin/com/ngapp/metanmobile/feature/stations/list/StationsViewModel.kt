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

package com.ngapp.metanmobile.feature.stations.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.data.util.SyncManager
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.feature.stations.list.state.StationsAction
import com.ngapp.metanmobile.feature.stations.list.state.StationsUiState
import com.ngapp.metanmobile.feature.stations.list.state.StationsUiState.Loading
import com.ngapp.metanmobile.feature.stations.list.state.StationsUiState.Success
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
class StationsViewModel @Inject constructor(
    syncManager: SyncManager,
    userStationsRepository: StationResourcesWithFavoritesRepository,
    private val locationsRepository: LocationsRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private var _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var _stationCode = MutableStateFlow("")
    val stationCode = _stationCode.asStateFlow()

    private var _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    val uiState: StateFlow<StationsUiState> = stationsUiState(
        searchQuery = searchQuery,
        userStationsRepository = userStationsRepository,
        locationsRepository = locationsRepository,
        userDataRepository = userDataRepository
    )
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = Loading,
        )

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = false,
        )


    fun triggerAction(action: StationsAction) {
        when (action) {
            is StationsAction.UpdateLocation -> onUpdateLocation(action.hasPermissions)
            is StationsAction.ShowAlertDialog -> onShowAlertDialog(action.showDialog)
            is StationsAction.UpdateSearchQuery -> onUpdateSearchQuery(action.input)
            is StationsAction.UpdateSortingConfig -> onUpdateSortingConfig(action.stationSortingConfig)
            is StationsAction.UpdateStationCode -> onUpdateStationCode(action.stationCode)
            is StationsAction.UpdateStationFavorite ->
                onUpdateStationFavorite(action.stationCode, action.favorite)
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

    private fun onUpdateStationCode(stationCode: String) {
        _stationCode.value = stationCode
    }
}

private fun stationsUiState(
    searchQuery: Flow<String>,
    userStationsRepository: StationResourcesWithFavoritesRepository,
    locationsRepository: LocationsRepository,
    userDataRepository: UserDataRepository,
): Flow<StationsUiState> {
    return searchQuery.flatMapLatest { query ->
        combine(
            userStationsRepository.observeAll(query = StationResourceQuery(searchQuery = query)),
            locationsRepository.getLocationResource(),
            userDataRepository.userData
        ) { stationList, userLocation, userData ->
            Success(
                stationList = stationList,
                userLocation = userLocation,
                stationSortingConfig = userData.stationSortingConfig,
            )
        }
    }
}