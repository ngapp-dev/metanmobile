/*
 * Copyright 2025 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.feature.stations.detail.newdetail.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngapp.metanmobile.core.designsystem.component.ButtonWithIcon
import com.ngapp.metanmobile.core.designsystem.component.MMFilledIconButton
import com.ngapp.metanmobile.core.designsystem.component.htmltext.HtmlText
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Black
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray500
import com.ngapp.metanmobile.core.designsystem.theme.Green
import com.ngapp.metanmobile.core.designsystem.theme.LightBlue
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.feature.stations.R
import java.util.Locale
import com.ngapp.metanmobile.core.designsystem.R as DesignSystemR
import com.ngapp.metanmobile.core.ui.R as CoreUiR

@Composable
internal fun HeaderWithStatusAndActions(
    title: String,
    modifier: Modifier = Modifier,
    onShareClick: () -> Unit,
    onCloseClick: () -> Unit,
    stationStatus: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MMTypography.displayMedium.copy(
                    letterSpacing = (-0.5).sp,
                    fontWeight = FontWeight.Normal
                ),
            )
            stationStatus()
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(end = 4.dp)
        ) {
            MMFilledIconButton(
                modifier = Modifier.size(32.dp),
                iconModifier = Modifier.size(18.dp),
                imageVector = MMIcons.Share,
                contentDescription = stringResource(R.string.feature_stations_description_share_station),
                onClick = onShareClick,
            )
            MMFilledIconButton(
                modifier = Modifier.size(32.dp),
                iconModifier = Modifier.size(18.dp),
                imageVector = MMIcons.Close,
                contentDescription = stringResource(DesignSystemR.string.core_designsystem_description_back),
                onClick = onCloseClick,
            )
        }
    }
}

@Composable
internal fun HeaderObjectType(
    objectType: String,
    distanceBetween: Double,
    style: TextStyle = MMTypography.bodyLarge,
) {
    val km =
        String.format(Locale.getDefault(), "%.1f", distanceBetween).replace('.', ',')
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = objectType,
            style = style,
        )
        if (distanceBetween != 0.0) {
            Text(
                text = "·",
                style = style,
            )
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = MMIcons.CarOutlined,
                contentDescription = stringResource(DesignSystemR.string.core_designsystem_description_car_icon),
                tint = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = stringResource(CoreUiR.string.core_ui_text_value_km, km),
                style = style,
            )
        }
    }
}

@Composable
internal fun HeaderObjectWorkTime(workingTime: String) {
    val is24Hours = workingTime.contains("круглосуточно")
    val displayText =
        if (is24Hours) stringResource(CoreUiR.string.core_ui_text_open_24_hours) else workingTime
    HtmlText(
        text = displayText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MMTypography.titleLarge,
        color = Green,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
internal fun HeaderObjectCNGPrice(cngPrice: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = MMIcons.GasStationOutlined,
            contentDescription = stringResource(DesignSystemR.string.core_designsystem_description_gas_station_icon),
            tint = MaterialTheme.colorScheme.onPrimary,
        )
        Text(
            text = stringResource(CoreUiR.string.core_ui_text_byn_per_one_meter, cngPrice),
            style = MMTypography.bodyLarge,
        )
    }
}

@Composable
internal fun HeaderButtons(
    isFavorite: Boolean,
    onDirectionsClick: () -> Unit,
    onCallClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShareClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ButtonWithIcon(
            imageVector = MMIcons.DirectionsFilled,
            textResId = R.string.feature_stations_button_directions,
            containerColor = Blue,
            contentColor = White,
            shape = ShapeDefaults.ExtraLarge,
            onClick = { onDirectionsClick() }
        )
        ButtonWithIcon(
            imageVector = MMIcons.CallFilled,
            textResId = R.string.feature_stations_button_call,
            containerColor = LightBlue,
            contentColor = Blue,
            shape = ShapeDefaults.ExtraLarge,
            onClick = { onCallClick() }
        )
        ButtonWithIcon(
            imageVector = if (isFavorite) MMIcons.Bookmark else MMIcons.BookmarkBorder,
            textResId = if (isFavorite) R.string.feature_stations_button_saved else R.string.feature_stations_button_save,
            containerColor = if (isFavorite) LightBlue.copy(alpha = 0.8f)
                .compositeOver(Gray500) else LightBlue,
            contentColor = if (isFavorite) Black else Blue,
            shape = ShapeDefaults.ExtraLarge,
            onClick = { onToggleBookmark() }
        )
        ButtonWithIcon(
            imageVector = MMIcons.Share,
            textResId = R.string.feature_stations_button_share,
            containerColor = LightBlue,
            contentColor = Blue,
            shape = ShapeDefaults.ExtraLarge,
            onClick = { onShareClick() }
        )
    }
}
