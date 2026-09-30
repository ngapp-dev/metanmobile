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
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun LanguageConfigRowItem(
    modifier: Modifier = Modifier,
    titleRes: StringResource,
    currentLanguage: String,
    onShowAlertDialog: () -> Unit,
) {
    val languageName = when (currentLanguage) {
        LanguageConfig.RU.name -> SharedRes.strings.feature_menu_main_pref_language_russian
        LanguageConfig.BE.name -> SharedRes.strings.feature_menu_main_pref_language_belarusian
        else -> SharedRes.strings.feature_menu_main_pref_language_english
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MMColors.cardBackgroundColor)
            .height(64.dp)
            .clickable(onClick = onShowAlertDialog)
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
            text = stringResource(languageName),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 4.dp),
            style = MMTypography.headlineMedium,
            color = Gray400
        )
    }
}
