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

package com.ngapp.metanmobile.feature.stations.list.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.logStationResourceOpened
import com.ngapp.metanmobile.core.ui.stations.StationRow
import com.ngapp.metanmobile.core.ui.stations.StationRowShimmer
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.core.ui.util.isGoogleServicesAvailable
import com.ngapp.metanmobile.feature.stations.list.state.StationsAction

@Composable
internal fun StationListContent(
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    stationsList: List<UserStationResource>,
    onAction: (StationsAction) -> Unit,
    onDetailClick: (String) -> Unit,
) {
    val permissionsState = LocalPermissionsState.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val isGoogleServicesAvailable = remember { isGoogleServicesAvailable(context) }
    val analyticsHelper = LocalAnalyticsHelper.current

    LazyVerticalGrid(
        state = gridState,
        modifier = modifier.animateContentSize(),
        columns = GridCells.Adaptive(300.dp),
    ) {
        if (stationsList.isNotEmpty()) {
            items(items = stationsList, key = { station -> station.code }) { station ->
                StationRow(
                    modifier = Modifier.fillMaxWidth(),
                    station = station,
                    onDetailClick = {
                        analyticsHelper.logStationResourceOpened(station.code)
                        onDetailClick(station.code)
                    },
                    onToggleBookmark = {
                        onAction(
                            StationsAction.UpdateStationFavorite(station.code, !station.isFavorite)
                        )
                    },
                    locationPermissionGranted = permissionsState.hasLocationPermissions,
                    isGoogleServicesAvailable = isGoogleServicesAvailable,
                    onPermissionRequestAgain = { permissionsState.requestPermissions() },
                    onGoogleServicesRequest = {
                        uriHandler.openUri("market://details?id=com.google.android.gms")
                    },
                )
            }
        } else {
            items(10) {
                StationRowShimmer(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}