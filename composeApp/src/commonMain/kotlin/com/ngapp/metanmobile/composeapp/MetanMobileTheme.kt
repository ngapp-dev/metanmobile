package com.ngapp.metanmobile.composeapp

import androidx.compose.runtime.Composable
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.designsystem.theme.shouldUseDarkTheme
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig

@Composable
fun MetanMobileTheme(
    darkThemeConfig: DarkThemeConfig,
    content: @Composable () -> Unit,
) = MMTheme(darkTheme = shouldUseDarkTheme(darkThemeConfig), content = content)
