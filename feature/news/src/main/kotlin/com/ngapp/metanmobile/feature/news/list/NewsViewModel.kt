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

package com.ngapp.metanmobile.feature.news.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.data.repository.news.NewsResourceQuery
import com.ngapp.metanmobile.core.data.repository.news.UserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.data.util.SyncManager
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.feature.news.list.state.NewsAction
import com.ngapp.metanmobile.feature.news.list.state.NewsUiState
import com.ngapp.metanmobile.feature.news.list.state.NewsUiState.Success
import com.ngapp.metanmobile.feature.news.list.state.NewsUiState.Loading
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
class NewsViewModel @Inject constructor(
    syncManager: SyncManager,
    userNewsResourceRepository: UserNewsResourceRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private var _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    val uiState: StateFlow<NewsUiState> = newsUiState(
        searchQuery = searchQuery,
        userNewsResourceRepository = userNewsResourceRepository,
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

    fun triggerAction(action: NewsAction) {
        when (action) {
            is NewsAction.ShowAlertDialog -> onShowAlertDialog(action.showDialog)
            is NewsAction.UpdateSearchQuery -> onUpdateSearchQuery(action.input)
            is NewsAction.UpdateSortingConfig -> onUpdateSortingConfig(action.newsSortingConfig)
        }
    }

    private fun onUpdateSortingConfig(newsSortingConfig: NewsSortingConfig) =
        viewModelScope.launch {
            userDataRepository.setNewsSortingConfig(newsSortingConfig)
        }

    private fun onUpdateSearchQuery(input: String) {
        _searchQuery.value = input
    }

    private fun onShowAlertDialog(showDialog: Boolean) {
        _showDialog.value = showDialog
    }
}

private fun newsUiState(
    searchQuery: Flow<String>,
    userNewsResourceRepository: UserNewsResourceRepository,
    userDataRepository: UserDataRepository,
): Flow<NewsUiState> {
    return searchQuery.flatMapLatest { query ->
        combine(
            userNewsResourceRepository.observeAll(query = NewsResourceQuery(filterNewsPinned = true)),
            userNewsResourceRepository.observeAll(
                query = NewsResourceQuery(
                    filterNewsPinned = false,
                    searchQuery = query
                )
            ),
            userDataRepository.userData,
        ) { pinnedNewsList, newsList, userData ->
            Success(
                pinnedNewsList = pinnedNewsList,
                newsList = newsList,
                newsSortingConfig = userData.newsSortingConfig
            )
        }
    }
}