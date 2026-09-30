package com.ngapp.metanmobile.feature.menu.state
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
sealed interface SettingsUiState { data object Loading : SettingsUiState; data class Success(val darkThemeConfig: DarkThemeConfig) : SettingsUiState }
