package com.ngapp.metanmobile.feature.stations.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.logStationResourceOpened
import com.ngapp.metanmobile.core.ui.stations.StationRow
import com.ngapp.metanmobile.core.ui.stations.StationRowShimmer
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.core.ui.util.isGoogleServicesAvailable
import com.ngapp.metanmobile.feature.stations.state.StationsAction

@Composable
internal fun StationListContent(
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    stationsList: List<UserStationResource>,
    onAction: (StationsAction) -> Unit,
    onDetailClick: (String) -> Unit,
) {
    val permissionsState = LocalPermissionsState.current
    val uriHandler = LocalUriHandler.current
    val isGoogleServicesAvailable = remember { isGoogleServicesAvailable() }
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
