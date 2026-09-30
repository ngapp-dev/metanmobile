package com.ngapp.metanmobile.feature.stations.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
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
import dev.icerock.moko.resources.ImageResource

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
                zoomControlsEnabled = false,
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
                                iconResource = MMIcons.StationClfs,
                                onMarkerClick = { onDetailClick(it, marker.latitude, marker.longitude) },
                            )
                        }

                        marker.type == StationType.CNG.typeName && marker.isOperate == 1 -> {
                            GoogleMapMarkerView(
                                cameraPositionState = cameraPositionState,
                                marker = marker,
                                iconResource = MMIcons.StationCng,
                                onMarkerClick = { onDetailClick(it, marker.latitude, marker.longitude) },
                            )
                        }

                        marker.type == StationType.SERVICE.typeName && marker.isOperate == 1 -> {
                            GoogleMapMarkerView(
                                cameraPositionState = cameraPositionState,
                                marker = marker,
                                iconResource = MMIcons.StationService,
                                onMarkerClick = { onDetailClick(it, marker.latitude, marker.longitude) },
                            )
                        }

                        else -> {
                            GoogleMapMarkerView(
                                cameraPositionState = cameraPositionState,
                                marker = marker,
                                iconResource = MMIcons.StationNotWorking,
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
    iconResource: ImageResource,
    onMarkerClick: (String) -> Unit = {},
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    val icon = bitmapDescriptor(LocalContext.current, iconResource)

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
    imageResource: ImageResource,
): BitmapDescriptor? {
    val drawable: Drawable = imageResource.getDrawable(context) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}
