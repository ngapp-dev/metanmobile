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

package com.ngapp.metanmobile.feature.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.feature.menu.state.SettingsAction
import com.ngapp.metanmobile.feature.menu.state.SettingsUiState
import com.ngapp.metanmobile.feature.menu.state.SettingsUiState.Loading
import com.ngapp.metanmobile.feature.menu.state.SettingsUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    val settingsUiState: StateFlow<SettingsUiState> = userDataRepository.userData
        .map { userData -> Success(darkThemeConfig = userData.darkThemeConfig) }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = Loading,
        )

    fun triggerAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.UpdateDarkThemeConfig -> onUpdateDarkThemeConfig(action.darkThemeConfig)
        }
    }

    private fun onUpdateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) = viewModelScope.launch {
        userDataRepository.setDarkThemeConfig(darkThemeConfig)
    }
}