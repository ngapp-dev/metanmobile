package com.ngapp.metanmobile.feature.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.feature.menu.state.SettingsAction
import com.ngapp.metanmobile.feature.menu.state.SettingsUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MenuViewModel(private val repository: UserDataRepository) : ViewModel() {
    val settingsUiState: StateFlow<SettingsUiState> =
        repository.userData.map { SettingsUiState.Success(it.darkThemeConfig) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState.Loading)

    fun triggerAction(action: SettingsAction) {
        if (action is SettingsAction.UpdateDarkThemeConfig) viewModelScope.launch {
            repository.setDarkThemeConfig(
                action.darkThemeConfig
            )
        }
    }
}
