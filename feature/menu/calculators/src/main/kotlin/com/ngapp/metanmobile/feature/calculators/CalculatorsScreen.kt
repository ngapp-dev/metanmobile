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

package com.ngapp.metanmobile.feature.calculators

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.designsystem.theme.textColor
import com.ngapp.metanmobile.core.ui.MetanMobileCalculators
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent

@Composable
internal fun CalculatorsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CalculatorsScreen(
        modifier = modifier,
        onBackClick = onBackClick
    )
}

@Composable
private fun CalculatorsScreen(
    modifier: Modifier,
    onBackClick: () -> Unit,
) {
    CalculatorsHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->
        MetanMobileCalculators(
            modifier = Modifier.padding(padding),
            tabRowIndicatorColor = MMColors.textColor,
            tabNameColor = MMColors.textColor
        )
    }
    TrackScreenViewEvent(screenName = "CalculatorsScreen")
}

@Composable
private fun CalculatorsHeader(
    modifier: Modifier,
    onBackClick: () -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MMToolbarWithNavIcon(
                titleResId = R.string.feature_menu_calculators_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Preview
@Composable
private fun CalculatorsScreenPreview() {
    MMTheme {
        CalculatorsScreen(
            modifier = Modifier,
            onBackClick = {},
        )
    }
}