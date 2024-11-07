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

package com.ngapp.metanmobile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.MainActivityUiState.Loading
import com.ngapp.metanmobile.MainActivityUiState.Success
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.home.HomeContentItem.CAREER
import com.ngapp.metanmobile.core.model.home.HomeContentItem.CALCULATORS
import com.ngapp.metanmobile.core.model.home.HomeContentItem.USER_LOCATION
import com.ngapp.metanmobile.core.model.home.HomeContentItem.FAQ
import com.ngapp.metanmobile.core.model.userdata.UserData
import com.ngapp.metanmobile.core.ui.ads.ConsentHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val consentHelper: ConsentHelper,
) : ViewModel() {

    private val _consentState = MutableStateFlow(ConsentState())
    val consentState: StateFlow<ConsentState> = _consentState.asStateFlow()

    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData
        .map(::Success)
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = Loading,
        )

    init {
        onObserveConsent()
        onTrackUsageTime()
        onInitUserData()
    }

    private fun onObserveConsent() {
        viewModelScope.launch {
            userDataRepository.userData.collectLatest { userData ->
                if (userData.shouldHideOnboarding) {
                    consentHelper.obtainConsentAndShow()
                }
            }
        }
        viewModelScope.launch {
            consentHelper.canShowAds.collectLatest { canShow ->
                _consentState.value = _consentState.value.copy(canShowAds = canShow)
            }
        }
    }

    private fun onTrackUsageTime() = viewModelScope.launch {
        userDataRepository.userData.collect { userData ->
            var usageTime = userData.totalUsageTime
            while (usageTime < 300_000) {
                delay(30_000)
                usageTime += 30_000
                userDataRepository.updateTotalUsageTime(usageTime)
                if (usageTime >= 300_000) {
                    break
                }
            }
        }
    }

    fun setReviewShown() = viewModelScope.launch {
        userDataRepository.setReviewShown(true)
    }

    private fun onInitUserData() = viewModelScope.launch {
        userDataRepository.userData.collectLatest { userData ->
            if (userData.homeReorderableList.isEmpty()) {
                userDataRepository.setHomeReorderableList(
                    listOf(USER_LOCATION, CALCULATORS, FAQ, CAREER)
                )
            }
        }
    }

}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userData: UserData) : MainActivityUiState
}

data class ConsentState(
    val canShowAds: Boolean = false,
    val errorMessage: String? = null,
)
