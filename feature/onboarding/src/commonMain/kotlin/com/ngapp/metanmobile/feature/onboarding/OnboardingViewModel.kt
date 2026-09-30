package com.ngapp.metanmobile.feature.onboarding

import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.feature.onboarding.state.OnboardingAction
import com.ngapp.metanmobile.feature.onboarding.state.OnboardingUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Shared ViewModel logic from the Android onboarding feature. */
class OnboardingViewModel(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    val uiState = userDataRepository.userData
        .map { if (it.shouldHideOnboarding) OnboardingUiState.NotShown else OnboardingUiState.Shown }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OnboardingUiState.Loading)

    fun triggerAction(action: OnboardingAction) {
        if (action is OnboardingAction.DismissOnboarding) {
            viewModelScope.launch {
                userDataRepository.setStationSortingConfig(StationSortingConfig.init())
                userDataRepository.setNewsSortingConfig(NewsSortingConfig.init())
                userDataRepository.setShouldHideOnboarding(true)
            }
        }
    }
}
