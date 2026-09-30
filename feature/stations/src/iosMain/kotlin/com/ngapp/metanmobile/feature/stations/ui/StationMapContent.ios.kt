package com.ngapp.metanmobile.feature.stations.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ngapp.metanmobile.core.model.location.LocationResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState

private val DEFAULT_CENTER = 53.90309661691656 to 27.55363993274304

@Composable
actual fun StationMapContent(
    modifier: Modifier,
    stationList: List<UserStationResource>,
    userLocation: LocationResource?,
    bottomSheetPartiallyExpanded: Boolean,
    onDetailClick: (String) -> Unit,
) {
    val permissionsState = LocalPermissionsState.current
    var center by rememberSaveable { mutableStateOf(DEFAULT_CENTER) }
    // Once-only flag: center on the user the first time a location fix actually arrives, same
    // "fly to user on load" moment as Android's shouldAnimateCamera — but folded into `center`
    // itself here (rather than a parallel one-shot camera animation) so a later bottom-sheet
    // expand/collapse re-centers on the user's location too, not back on the map's hardcoded
    // fallback coordinates.
    var hasCenteredOnUser by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(userLocation, permissionsState.hasLocationPermissions) {
        if (!hasCenteredOnUser && permissionsState.hasLocationPermissions && userLocation != null) {
            center = userLocation.latitude to userLocation.longitude
            hasCenteredOnUser = true
        }
    }

    MapKitView(
        modifier = modifier,
        mapItems = stationList,
        center = center,
        bottomSheetPartiallyExpanded = bottomSheetPartiallyExpanded,
        locationPermissionGranted = permissionsState.hasLocationPermissions,
        onRequirePermissions = { permissionsState.requestPermissions() },
        onMyLocationClick = {
            val lat = userLocation?.latitude
            val long = userLocation?.longitude
            if (lat != null && long != null) center = lat to long
        },
        onDetailClick = { code, latitude, longitude ->
            onDetailClick(code)
            val lat = latitude.toDoubleOrNull()
            val long = longitude.toDoubleOrNull()
            if (lat != null && long != null) center = lat to long
        },
    )
}
