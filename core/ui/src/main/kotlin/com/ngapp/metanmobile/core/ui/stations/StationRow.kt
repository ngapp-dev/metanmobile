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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ngapp.metanmobile.core.designsystem.component.ProgressIndicatorSmall
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Gray500
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.R
import java.util.Locale

@Composable
fun StationRow(
    modifier: Modifier = Modifier,
    station: UserStationResource,
    locationPermissionGranted: Boolean,
    isGoogleServicesAvailable: Boolean,
    onPermissionRequestAgain: () -> Unit,
    onGoogleServicesRequest: () -> Unit,
    onDetailClick: () -> Unit,
    onToggleBookmark: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(MMColors.cardBackgroundColor)
            .clickable(onClick = onDetailClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(station.previewPicture)
                .crossfade(true)
                .build(),
            error = painterResource(MMIcons.Error),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(R.string.core_ui_description_station_list_img),
            modifier = Modifier
                .size(82.dp)
                .padding(vertical = 8.dp)
                .padding(end = 8.dp)
                .clip(RoundedCornerShape(20.dp, 0.dp, 20.dp, 0.dp)),
        )
        Column(
            modifier = modifier
                .weight(1f)
                .fillMaxSize()
                .padding(all = 4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = station.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier,
                    style = MMTypography.titleLarge
                )
                StationStatusView(station)
            }
            Text(
                text = station.address,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp, end = 8.dp),
                style = MMTypography.titleMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!locationPermissionGranted) {
                    CompositionLocalProvider(
                        LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
                    ) {
                        IconButton(
                            onClick = { onPermissionRequestAgain.invoke() },
                            modifier = Modifier.padding(all = 0.dp)
                        ) {
                            Icon(
                                painter = rememberVectorPainter(MMIcons.LocationOff),
                                contentDescription = stringResource(R.string.core_ui_description_location_off_img),
                                tint = Gray500
                            )
                        }
                    }
                } else if (!isGoogleServicesAvailable) {
                    CompositionLocalProvider(
                        LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
                    ) {
                        IconButton(
                            onClick = { onGoogleServicesRequest.invoke() },
                            modifier = Modifier.padding(all = 0.dp)
                        ) {
                            Icon(
                                painter = rememberVectorPainter(MMIcons.LocationOff),
                                contentDescription = stringResource(R.string.core_ui_description_location_off_img),
                                tint = Gray500
                            )
                        }
                    }
                } else {
                    if (station.distanceBetween == 0.0) {
                        ProgressIndicatorSmall()
                    } else {
                        val km =
                            String.format(Locale.getDefault(), "%.1f", station.distanceBetween)
                                .replace('.', ',')
                        Text(
                            text = stringResource(id = R.string.core_ui_text_value_km, km),
                            maxLines = 1,
                            overflow = TextOverflow.Visible,
                            style = MMTypography.bodySmall
                        )
                    }
                }
            }
        }
        MMFavButton(
            favorite = station.isFavorite,
            stationTitle = station.title,
            onClick = onToggleBookmark
        )
    }
}