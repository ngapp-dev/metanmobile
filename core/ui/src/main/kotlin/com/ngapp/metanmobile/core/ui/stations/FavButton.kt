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

package com.ngapp.metanmobile.core.ui.stations

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ngapp.metanmobile.core.designsystem.component.MMIconToggleButton
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Gray500
import com.ngapp.metanmobile.core.designsystem.theme.Red700
import com.ngapp.metanmobile.core.ui.R

@Composable
fun MMFavButton(
    favorite: Boolean,
    stationTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MMIconToggleButton(
        checked = favorite,
        onCheckedChange = { onClick() },
        modifier = modifier,
        icon = {
            Icon(
                imageVector = MMIcons.FavoritesOutlined,
                contentDescription = stringResource(R.string.core_ui_description_like_icon, stationTitle),
                tint = Gray500
            )
        },
        checkedIcon = {
            Icon(
                imageVector = MMIcons.FavoritesFilled,
                contentDescription = stringResource(R.string.core_ui_description_dislike_icon, stationTitle),
                tint = Red700
            )
        },
    )
}