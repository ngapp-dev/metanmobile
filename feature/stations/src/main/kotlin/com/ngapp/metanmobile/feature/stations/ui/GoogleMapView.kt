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

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.toolbarIconColor
import com.ngapp.metanmobile.core.model.station.StationType
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.logStationResourceOpened

@Composable
internal fun GoogleMapView(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    mapItems: List<UserStationResource>?,
    locationPermissionGranted: Boolean,
    onDetailClick: (String, String, String) -> Unit,
    onRequirePermissions: () -> Unit = {},
    onMyLocationClick: () -> Unit = {},
    onMapLoaded: () -> Unit = {}
) {
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = false,
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false
            )
        )
    }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = locationPermissionGranted
            )
        )
    }
    val mapVisible by remember { mutableStateOf(true) }

    if (mapVisible) {
        Box(modifier.fillMaxSize()) {
            GoogleMap(
                modifier = modifier,
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = uiSettings,
                onMapLoaded = { onMapLoaded() },
                onPOIClick = {
                    Log.d("GoogleMapView", "POI clicked: ${it.name}")
                }
            ) {
                mapItems?.forEach { marker ->
                    when {
                        marker.type == StationType.CLFS.typeName && marker.isOperate == 1 -> {
                            GoogleMapMarkerView(
                                cameraPositionState = cameraPositionState,
                                marker = marker,
                                iconResourceId = MMIcons.StationClfs,
                                onMarkerClick = { onDetailClick(it, marker.latitude, marker.longitude) },
                            )
                        }

                        marker.type == StationType.CNG.typeName && marker.isOperate == 1 -> {
                            GoogleMapMarkerView(
                                cameraPositionState = cameraPositionState,
                                marker = marker,
                                iconResourceId = MMIcons.StationCng,
                                onMarkerClick = { onDetailClick(it, marker.latitude, marker.longitude) },
                            )
                        }

                        marker.type == StationType.SERVICE.typeName && marker.isOperate == 1 -> {
                            GoogleMapMarkerView(
                                cameraPositionState = cameraPositionState,
                                marker = marker,
                                iconResourceId = MMIcons.StationService,
                                onMarkerClick = { onDetailClick(it, marker.latitude, marker.longitude) },
                            )
                        }

                        else -> {
                            GoogleMapMarkerView(
                                cameraPositionState = cameraPositionState,
                                marker = marker,
                                iconResourceId = MMIcons.StationNotWorking,
                                onMarkerClick = { onDetailClick(it, marker.latitude, marker.longitude) },
                            )
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    if (!locationPermissionGranted) {
                        onRequirePermissions()
                    } else {
                        onMyLocationClick()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 24.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(
                    imageVector = if (!locationPermissionGranted) {
                        MMIcons.LocationDisabled
                    } else {
                        MMIcons.MyLocation
                    },
                    contentDescription = "Add FAB",
                    tint = MMColors.toolbarIconColor,
                )
            }
        }
    }
}

@Composable
private fun GoogleMapMarkerView(
    cameraPositionState: CameraPositionState,
    marker: UserStationResource,
    @DrawableRes iconResourceId: Int,
    onMarkerClick: (String) -> Unit = {},
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    val icon = bitmapDescriptor(LocalContext.current, iconResourceId)

    val markerClick: (Marker) -> Boolean = {
        cameraPositionState.projection?.let { _ ->
            analyticsHelper.logStationResourceOpened(it.snippet.orEmpty())
            onMarkerClick.invoke(it.snippet.orEmpty())
        }
        false
    }
    MarkerInfoWindowContent(
        state = MarkerState(
            position = LatLng(
                marker.latitude.toDouble(),
                marker.longitude.toDouble()
            )
        ),
        icon = icon,
        snippet = marker.code,
        title = marker.title,
        onClick = markerClick,
        draggable = true,
    ) {
        Text(it.title ?: "Title", color = Color.Red)
    }
}

private fun bitmapDescriptor(
    context: Context,
    vectorResId: Int,
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}