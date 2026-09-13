package com.ngapp.metanmobile.feature.menu.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.Red700
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun LanguageConfigDialog(
    modifier: Modifier = Modifier,
    currentLanguage: String,
    onChangeLanguageConfig: (selectedLanguage: String) -> Unit,
    onShowAlertDialog: (Boolean) -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(SharedRes.strings.feature_menu_main_title_app_language),
                style = MMTypography.displayMedium
            )
        },
        text = {
            Column(Modifier.selectableGroup()) {
                LanguageDialogChooserRow(
                    text = stringResource(SharedRes.strings.feature_menu_main_pref_language_russian),
                    selected = currentLanguage == LanguageConfig.RU.name,
                    onClick = {
                        onChangeLanguageConfig(LanguageConfig.RU.name)
                        onShowAlertDialog(false)
                    },
                )
                LanguageDialogChooserRow(
                    text = stringResource(SharedRes.strings.feature_menu_main_pref_language_belarusian),
                    selected = currentLanguage == LanguageConfig.BE.name,
                    onClick = {
                        onChangeLanguageConfig(LanguageConfig.BE.name)
                        onShowAlertDialog(false)
                    },
                )
                LanguageDialogChooserRow(
                    text = stringResource(SharedRes.strings.feature_menu_main_pref_language_english),
                    selected = currentLanguage == LanguageConfig.EN.name,
                    onClick = {
                        onChangeLanguageConfig(LanguageConfig.EN.name)
                        onShowAlertDialog(false)
                    },
                )
            }
        },
        onDismissRequest = { onShowAlertDialog(false) },
        confirmButton = {},
    )
    TrackScreenViewEvent(screenName = "LanguageConfigDialog")
}

@Composable
private fun LanguageDialogChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .offset((-12).dp)
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Red700,
                unselectedColor = Blue,
                disabledUnselectedColor = Gray400,
                disabledSelectedColor = Gray400
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}
