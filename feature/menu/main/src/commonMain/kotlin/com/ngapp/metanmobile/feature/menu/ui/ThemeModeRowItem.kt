package com.ngapp.metanmobile.feature.menu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun ThemeModeRowItem(
    modifier: Modifier = Modifier,
    titleRes: StringResource,
    themeMode: DarkThemeConfig,
    onOpenAlertDialog: () -> Unit,
) {
    val themeModeName = when (themeMode) {
        DarkThemeConfig.LIGHT -> SharedRes.strings.feature_menu_main_text_theme_mode_light
        DarkThemeConfig.DARK -> SharedRes.strings.feature_menu_main_text_theme_mode_dark
        DarkThemeConfig.FOLLOW_SYSTEM -> SharedRes.strings.feature_menu_main_theme_mode_system_default
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MMColors.cardBackgroundColor)
            .height(64.dp)
            .clickable(onClick = onOpenAlertDialog)
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(titleRes),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp),
            style = MMTypography.titleLarge
        )
        Text(
            text = stringResource(themeModeName),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 4.dp),
            style = MMTypography.headlineMedium,
            color = Gray400
        )
    }
}
