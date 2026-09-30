package com.ngapp.metanmobile.feature.menu.state
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
sealed interface SettingsAction { data class UpdateDarkThemeConfig(val darkThemeConfig: DarkThemeConfig) : SettingsAction }
