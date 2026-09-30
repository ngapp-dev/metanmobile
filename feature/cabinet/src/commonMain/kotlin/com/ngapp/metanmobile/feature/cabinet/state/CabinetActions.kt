package com.ngapp.metanmobile.feature.cabinet.state

sealed interface CabinetActions {
    data class UpdateUiState(val uiState: CabinetUiState) : CabinetActions
}
