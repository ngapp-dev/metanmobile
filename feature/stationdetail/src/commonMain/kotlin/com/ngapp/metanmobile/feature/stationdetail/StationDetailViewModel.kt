package com.ngapp.metanmobile.feature.stationdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.domain.repository.news.NewsResourceQuery
import com.ngapp.metanmobile.core.domain.repository.news.UserNewsResourceRepository
import com.ngapp.metanmobile.core.domain.repository.price.PricesRepository
import com.ngapp.metanmobile.core.domain.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.domain.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.ShareManager
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailAction
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailUiState
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailUiState.Success
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Ported 1:1 from master's Android StationDetailViewModel. */
class StationDetailViewModel(
    private val userStationsRepository: StationResourcesWithFavoritesRepository,
    private val fuelPricesRepository: PricesRepository,
    private val userNewsResourceRepository: UserNewsResourceRepository,
    private val userDataRepository: UserDataRepository,
    private val shareManager: ShareManager,
) : ViewModel() {

    private val _stationCode = MutableStateFlow("")
    val stationCode: StateFlow<String> = _stationCode.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StationDetailUiState> = _stationCode
        .flatMapLatest { code ->
            if (code.isEmpty()) flowOf(StationDetailUiState.Loading)
            else stationDetailUiState(
                stationCode = code,
                userStationsRepository = userStationsRepository,
                userNewsResourceRepository = userNewsResourceRepository,
                fuelPricesRepository = fuelPricesRepository,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StationDetailUiState.Loading,
        )

    fun triggerAction(action: StationDetailAction) {
        when (action) {
            is StationDetailAction.SetStationCode -> setStationCode(action.stationCode)
            is StationDetailAction.UpdateStationFavorite ->
                onUpdateStationFavorite(action.stationCode, action.favorite)

            is StationDetailAction.ShareStation -> onShareStation(action.station)
        }
    }

    private fun setStationCode(stationCode: String?) {
        _stationCode.value = stationCode.orEmpty()
    }

    private fun onUpdateStationFavorite(stationCode: String, favorite: Boolean) {
        viewModelScope.launch {
            userDataRepository.setStationResourceFavorite(stationCode, favorite)
        }
    }

    private fun onShareStation(station: UserStationResource?) {
        shareManager.createShareStationIntent(station)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun stationDetailUiState(
    stationCode: String,
    userStationsRepository: StationResourcesWithFavoritesRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
    fuelPricesRepository: PricesRepository,
): Flow<StationDetailUiState> {

    val stationFlow = userStationsRepository.observeAll(
        query = StationResourceQuery(filterStationCodes = setOf(stationCode))
    ).map { it.firstOrNull() }

    val fuelPriceFlow = fuelPricesRepository.getFuelPrice()

    val relatedNewsFlow = stationFlow.flatMapLatest { station ->
        userNewsResourceRepository.observeAll(
            query = NewsResourceQuery(filterNewsByStationTitle = station?.title)
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
