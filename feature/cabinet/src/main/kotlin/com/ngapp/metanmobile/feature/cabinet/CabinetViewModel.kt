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

package com.ngapp.metanmobile.feature.cabinet

import androidx.lifecycle.ViewModel
import com.ngapp.metanmobile.feature.cabinet.state.CabinetActions
import com.ngapp.metanmobile.feature.cabinet.state.CabinetUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CabinetViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CabinetUiState())
    val uiState = _uiState.asStateFlow()

    fun triggerAction(action: CabinetActions) {
        when (action) {
            is CabinetActions.UpdateUiState -> onUpdateUiState(action.uiState)
        }
    }

    private fun onUpdateUiState(uiState: CabinetUiState) {
        _uiState.update { it.copy(isError = uiState.isError, isLoading = uiState.isLoading) }
    }
}