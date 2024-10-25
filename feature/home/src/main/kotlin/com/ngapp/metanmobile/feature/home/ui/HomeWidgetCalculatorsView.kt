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

package com.ngapp.metanmobile.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.ui.MetanMobileCalculators
import com.ngapp.metanmobile.feature.home.state.HomeAction
import com.ngapp.metanmobile.feature.home.state.HomeUiState
import com.ngapp.metanmobile.core.ui.R as CoreUiR

@Composable
internal fun HomeWidgetCalculatorsView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Blue)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = CoreUiR.string.core_ui_title_calculate_profit),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier,
            color = White,
            style = MMTypography.displayLarge
        )
        MetanMobileCalculators(
            tabRowIndicatorColor = White,
            tabNameColor = White,
        )
    }
}



