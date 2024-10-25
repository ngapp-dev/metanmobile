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

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.ngapp.metanmobile.core.model.location.LocationResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState

@Composable
internal fun StationMapContent(
    modifier: Modifier = Modifier,
    stationList: List<UserStationResource>,
    userLocation: LocationResource?,
    onDetailClick: (String) -> Unit,
) {
    val permissionsState = LocalPermissionsState.current
    val center = LatLng(53.90309661691656, 27.55363993274304)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(center, 10f)
    }

    var shouldAnimateCamera by remember { mutableStateOf(false) }

    LaunchedEffect(shouldAnimateCamera, userLocation) {
        if (shouldAnimateCamera && permissionsState.hasLocationPermissions && userLocation != null) {
            val location = LatLng(userLocation.latitude, userLocation.longitude)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(location, 15f, 0f, 0f)
                )
            )
            shouldAnimateCamera = false
        }
    }

    GoogleMapView(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        mapItems = stationList,
        locationPermissionGranted = permissionsState.hasLocationPermissions,
        onRequirePermissions = { permissionsState.requestPermissions() },
        onMyLocationClick = {
            Log.e("MyLocation", "MyLocation: ${userLocation?.latitude}|${userLocation?.longitude}")
            val location =
                LatLng(userLocation?.latitude ?: 0.0, userLocation?.longitude ?: 0.0)
            cameraPositionState.move(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(location, 15f, 0f, 0f)
                )
            )
        },
        onDetailClick = onDetailClick,
        onMapLoaded = { shouldAnimateCamera = true }
    )
}

