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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import com.ngapp.metanmobile.feature.menu.R

@Composable
internal fun LanguageConfigRowItem(
    modifier: Modifier = Modifier,
    titleResId: Int,
    currentLanguage: String,
    onShowAlertDialog: () -> Unit,
) {
    val languageName = when (currentLanguage) {
        LanguageConfig.RU.name -> R.string.feature_menu_main_pref_language_russian
        LanguageConfig.BE.name -> R.string.feature_menu_main_pref_language_belarusian
        else -> R.string.feature_menu_main_pref_language_english
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
            text = stringResource(id = titleResId),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp),
            style = MMTypography.titleLarge
        )
        Text(
            text = stringResource(id = languageName),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 4.dp),
            style = MMTypography.headlineMedium,
            color = Gray400
        )
    }
}