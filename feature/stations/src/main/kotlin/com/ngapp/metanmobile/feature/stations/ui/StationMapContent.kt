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

package com.ngapp.metanmobile.feature.stations.ui

import android.graphics.Point
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
    bottomSheetPartiallyExpanded: Boolean,
    onDetailClick: (String) -> Unit,
) {
    val permissionsState = LocalPermissionsState.current
    var center by rememberSaveable { mutableStateOf(LatLng(53.90309661691656, 27.55363993274304)) }

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
    LaunchedEffect(center, bottomSheetPartiallyExpanded) {
        val projection = cameraPositionState.projection ?: return@LaunchedEffect
        val screenHeight = projection.visibleRegion.latLngBounds.let {
            projection.toScreenLocation(it.northeast).y - projection.toScreenLocation(it.southwest).y
        }

        val screenLocation = projection.toScreenLocation(center)
        val offsetY = if (bottomSheetPartiallyExpanded) {
            (screenHeight * 0.2).toInt()
        } else 0

        val adjustedPoint = Point(screenLocation.x, screenLocation.y - offsetY)
        val adjustedLatLng = projection.fromScreenLocation(adjustedPoint)

        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(adjustedLatLng, 15f, 0f, 0f)
            )
        )
        shouldAnimateCamera = false
    }

    GoogleMapView(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        mapItems = stationList,
        locationPermissionGranted = permissionsState.hasLocationPermissions,
        onRequirePermissions = { permissionsState.requestPermissions() },
        onMyLocationClick = {
            val lat = userLocation?.latitude
            val long = userLocation?.longitude
            if (lat != null && long != null) {
                center = LatLng(lat, long)
            }
        },
        onDetailClick = { stationCode, latitude, longitude ->
            onDetailClick(stationCode)
            val lat = latitude.toDoubleOrNull()
            val long = longitude.toDoubleOrNull()
            if (lat != null && long != null) {
                center = LatLng(lat, long)
            }
        },
        onMapLoaded = { shouldAnimateCamera = true }
    )
}