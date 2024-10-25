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

package com.ngapp.metanmobile.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.designsystem.theme.MMShapes
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.lottie.LottieLoadingView
import com.ngapp.metanmobile.core.ui.stations.StationStatusView
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.core.ui.util.isGoogleServicesAvailable
import java.util.Locale

@Composable
fun WidgetPriceAndLocationView(
    nearestStation: UserStationResource?,
    cngPrice: PriceResource?,
    subtitle: Int = R.string.core_ui_text_nearest_station,
    onStationDetailClick: (String) -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Top
    ) {
        LeftWidgetView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f)
                .padding(vertical = 2.dp),
            cngPrice = cngPrice
        )
        RightWidgetView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f)
                .padding(vertical = 2.dp),
            nearestStation = nearestStation,
            subtitle = subtitle,
            onStationDetailClick = onStationDetailClick,
        )
    }
}

@Composable
fun LeftWidgetView(
    modifier: Modifier = Modifier,
    cngPrice: PriceResource?,
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .clip(MMShapes.large)
            .background(MaterialTheme.colorScheme.onBackground)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            if (cngPrice == null) {
                LottieLoadingView()
            } else {
                Text(
                    text = stringResource(id = R.string.core_ui_text_value_byn, cngPrice.content),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MMTypography.displayMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.core_ui_text_cng_price),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = stringResource(R.string.core_ui_text_for_one_meter),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun RightWidgetView(
    modifier: Modifier = Modifier,
    nearestStation: UserStationResource?,
    subtitle: Int,
    onStationDetailClick: (String) -> Unit = {},
) {
    val permissionsState = LocalPermissionsState.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val isGoogleServicesAvailable = remember { isGoogleServicesAvailable(context) }
    val analyticsHelper = LocalAnalyticsHelper.current

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .clip(MMShapes.large)
            .background(MaterialTheme.colorScheme.onBackground)
            .clickable {
                analyticsHelper.logStationResourceOpened(stationCode = nearestStation?.code.orEmpty())
                if (nearestStation != null) {
                    onStationDetailClick(nearestStation.code)
                }
            },
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            if (permissionsState.hasLocationPermissions) {
                if (nearestStation == null || nearestStation.distanceBetween == 0.0) {
                    LottieLoadingView()
                } else {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val distanceBetween =
                            String.format(
                                Locale.getDefault(),
                                "%.1f",
                                nearestStation.distanceBetween
                            ).replace('.', ',')
                        Text(
                            text = stringResource(
                                id = R.string.core_ui_text_value_km,
                                distanceBetween
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MMTypography.displayMedium,
                            modifier = Modifier
                        )
                        StationStatusView(nearestStation)
                    }
                    Text(
                        text = stringResource(id = subtitle),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = nearestStation.address,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .fillMaxWidth()
                    )
                }
            } else if (!isGoogleServicesAvailable) {
                RequestPermissionOrGoogleServices(
                    modifier = Modifier,
                    titleText = R.string.core_ui_text_google_service_unavailable,
                    buttonText = R.string.core_ui_button_google_service_update,
                    onClick = { uriHandler.openUri("market://details?id=com.google.android.gms") }
                )
            } else {
                RequestPermissionOrGoogleServices(
                    modifier = Modifier,
                    titleText = R.string.core_ui_text_permission_denied,
                    buttonText = R.string.core_ui_button_permission_request,
                    onClick = {
                        permissionsState.requestPermissions()
                    }
                )
            }
        }
    }
}