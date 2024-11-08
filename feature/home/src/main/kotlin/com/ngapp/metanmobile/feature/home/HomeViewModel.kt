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

package com.ngapp.metanmobile.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.data.repository.career.CareersRepository
import com.ngapp.metanmobile.core.data.repository.faq.FaqRepository
import com.ngapp.metanmobile.core.data.repository.faq.FaqResourceQuery
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.data.repository.news.NewsResourceQuery
import com.ngapp.metanmobile.core.data.repository.news.UserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.price.PricesRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.data.util.SyncManager
import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.feature.home.state.HomeAction
import com.ngapp.metanmobile.feature.home.state.HomeUiState
import com.ngapp.metanmobile.feature.home.state.HomeUiState.Loading
import com.ngapp.metanmobile.feature.home.state.HomeUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    syncManager: SyncManager,
    userStationsRepository: StationResourcesWithFavoritesRepository,
    fuelPricesRepository: PricesRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
    faqRepository: FaqRepository,
    careersRepository: CareersRepository,
    private val locationsRepository: LocationsRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = homeUiState(
        userStationsRepository = userStationsRepository,
        fuelPricesRepository = fuelPricesRepository,
        userNewsResourceRepository = userNewsResourceRepository,
        faqRepository = faqRepository,
        careersRepository = careersRepository,
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

    private var _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()

    private val _reorderableList = MutableStateFlow<List<HomeContentItem>>(emptyList())
    val reorderableList: StateFlow<List<HomeContentItem>> = _reorderableList.asStateFlow()

    private val _isLastNewsExpanded = MutableStateFlow(true)
    val isLastNewsExpanded: StateFlow<Boolean> = _isLastNewsExpanded.asStateFlow()

    init {
        viewModelScope.launch {
            userDataRepository.userData.collect { userData ->
                _reorderableList.value = userData.homeReorderableList
                _isLastNewsExpanded.value = userData.homeLastNewsExpanded
            }
        }
    }

    fun triggerAction(action: HomeAction) {
        when (action) {
            is HomeAction.UpdateLocation -> onUpdateLocation(action.hasPermissions)
            is HomeAction.EditUi -> onEditUi()
            is HomeAction.SaveUi -> onSaveUi()
            is HomeAction.ReorderList -> onReorderList(action.newOrder)
            is HomeAction.ExpandLastNews -> onExpandLastNews(action.expand)
        }
    }

    private fun onUpdateLocation(hasPermission: Boolean) = viewModelScope.launch {
        locationsRepository.updateLocation(hasPermission)
    }

    private fun onEditUi() {
        _isEditing.value = true
    }

    private fun onSaveUi() {
        _isEditing.value = false
        viewModelScope.launch {
            userDataRepository.setHomeReorderableList(reorderableList.value)
            userDataRepository.setHomeExpandedLastNews(isLastNewsExpanded.value)
        }
    }

    private fun onReorderList(newOrder: List<HomeContentItem>) {
        _reorderableList.value = newOrder
    }

    private fun onExpandLastNews(expand: Boolean) {
        _isLastNewsExpanded.value = expand
    }
}

private fun homeUiState(
    userStationsRepository: StationResourcesWithFavoritesRepository,
    fuelPricesRepository: PricesRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
    faqRepository: FaqRepository,
    careersRepository: CareersRepository,
): Flow<HomeUiState> {
    val pinnedNewsFlow =
        userNewsResourceRepository.observeAll(query = NewsResourceQuery(filterNewsPinned = true))
    val lastNewsFlow =
        userNewsResourceRepository.observeAll(query = NewsResourceQuery(filterNewsPinned = false))
    val fuelPriceFlow = fuelPricesRepository.getFuelPrice()
    val stationsFlow = userStationsRepository.observeAll(query = StationResourceQuery())
    val pinnedFaqFlow =
        faqRepository.getFaqList(query = FaqResourceQuery(filterFaqListPinned = true))
    val careersFlow = careersRepository.getCareerList()

    return combine(
        listOf(
            pinnedNewsFlow,
            lastNewsFlow,
            fuelPriceFlow,
            stationsFlow,
            pinnedFaqFlow,
            careersFlow
        )
    ) { flows: Array<Any?> ->
        val pinnedNewsList = flows[0] as List<UserNewsResource>
        val lastNewsList = flows[1] as List<UserNewsResource>
        val fuelPrice = flows[2] as PriceResource?
        val stationsList = flows[3] as List<UserStationResource>
        val pinnedFaqList = flows[4] as List<FaqResource>
        val careersList = flows[5] as List<CareerResource>

        val nearestStation = stationsList.minByOrNull { it.distanceBetween }

        Success(
            pinnedNewsList = pinnedNewsList,
            lastNewsList = lastNewsList.take(3),
            cngPrice = fuelPrice,
            nearestStation = nearestStation,
            pinnedFaqList = pinnedFaqList,
            career = careersList.getOrNull(0),
        )
    }
}
