package com.ngapp.metanmobile.feature.cabinet

import androidx.lifecycle.ViewModel
import com.ngapp.metanmobile.feature.cabinet.state.CabinetActions
import com.ngapp.metanmobile.feature.cabinet.state.CabinetUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CabinetViewModel : ViewModel() {

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
