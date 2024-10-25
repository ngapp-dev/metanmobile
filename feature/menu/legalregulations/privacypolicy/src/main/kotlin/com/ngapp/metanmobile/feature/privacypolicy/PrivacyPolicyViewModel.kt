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

package com.ngapp.metanmobile.feature.privacypolicy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.ui.ads.ConsentHelper
import com.ngapp.metanmobile.feature.privacypolicy.state.PrivacyPolicyAction
import com.ngapp.metanmobile.feature.privacypolicy.state.PrivacyPolicyUiState
import com.ngapp.metanmobile.feature.privacypolicy.state.PrivacyPolicyUiState.Loading
import com.ngapp.metanmobile.feature.privacypolicy.state.PrivacyPolicyUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor(
    userDataRepository: UserDataRepository,
    private val consentHelper: ConsentHelper,
) : ViewModel() {

    val isPrivacyOptionsRequired: StateFlow<Boolean> = flow {
        emit(consentHelper.isPrivacyOptionsRequired())
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5_000),
        initialValue = false
    )

    val uiState: StateFlow<PrivacyPolicyUiState> = userDataRepository.userData
        .map { userData -> Success(languageConfig = userData.languageConfig) }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = Loading,
        )

    fun triggerAction(action: PrivacyPolicyAction) {
        when (action) {
            is PrivacyPolicyAction.UpdateConsent -> onUpdateConsent()
        }
    }

    private fun onUpdateConsent() {
        if (isPrivacyOptionsRequired.value) {
            consentHelper.updateConsent()
        }
    }
}