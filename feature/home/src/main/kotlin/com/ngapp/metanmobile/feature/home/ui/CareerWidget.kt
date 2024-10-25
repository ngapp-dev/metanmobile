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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.component.MMTextButton
import com.ngapp.metanmobile.core.designsystem.theme.BluePale
import com.ngapp.metanmobile.core.designsystem.theme.GreenPale
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.ui.R
import com.ngapp.metanmobile.core.ui.career.CareerRow

@Composable
internal fun CareerWidget(
    career: CareerResource,
    onSeeAllClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(colors = listOf(BluePale, BluePale, GreenPale))
            )
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.core_ui_text_recent_offers),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MMTypography.displayLarge,
        )
        Spacer(Modifier.height(12.dp))
        CareerRow(career)
        Spacer(Modifier.height(16.dp))
        MMTextButton(
            onClick = onSeeAllClick,
            colors = ButtonDefaults.textButtonColors(
                containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            ),
            text = {
                Text(
                    text = stringResource(id = R.string.core_ui_button_show_all).uppercase(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    style = MMTypography.displaySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}