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

package com.ngapp.metanmobile.feature.favorites.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.component.ButtonWithIcon
import com.ngapp.metanmobile.core.designsystem.component.MMTextButton
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.feature.favorites.R
import com.ngapp.metanmobile.core.ui.R as CoreUiR

@Composable
internal fun FavoritesBottomSheetContent(
    station: UserStationResource,
    onApprove: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(MMColors.secondary)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.7f)) {
                Text(
                    text = stringResource(id = R.string.feature_favorites_text_favor_delete),
                    modifier = Modifier.padding(start = 16.dp),
                    color = Blue,
                    style = MMTypography.displaySmall
                )
                Text(
                    text = station.title,
                    modifier = Modifier.padding(start = 16.dp),
                    style = MMTypography.headlineMedium
                )
            }
            MMTextButton(
                onClick = onCancel,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(0.3f),
                contentPadding = PaddingValues(
                    start = 6.dp,
                    top = 0.dp,
                    end = 10.dp,
                    bottom = 0.dp
                ),
                text = {
                    Icon(
                        imageVector = MMIcons.CancelFilled,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        tint = Blue
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(id = CoreUiR.string.core_ui_button_cancel),
                        textAlign = TextAlign.End,
                        style = MMTypography.titleLarge,
                    )
                }
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
        Text(
            text = stringResource(
                id = R.string.feature_favorites_text_delete_favor_description,
                station.title
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            style = MMTypography.bodyLarge
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
        ButtonWithIcon(
            onClick = { onApprove() },
            icon = MMIcons.DeleteFilled,
            iconTint = White,
            textResId = R.string.feature_favorites_text_approve_remove,
            buttonBackgroundColor = Blue,
            fontColor = White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        )
    }
}