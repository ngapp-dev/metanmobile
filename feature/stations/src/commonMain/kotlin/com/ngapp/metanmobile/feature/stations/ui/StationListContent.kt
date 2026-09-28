package com.ngapp.metanmobile.feature.stations.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.designsystem.component.LocalMMFloatingBarPadding
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.ads.NativeBanner
import com.ngapp.metanmobile.core.ui.ads.NativeBannerLayout
import com.ngapp.metanmobile.core.ui.ads.isNativeBannerSlot
import com.ngapp.metanmobile.core.ui.logStationResourceOpened
import com.ngapp.metanmobile.core.ui.stations.StationRow
import com.ngapp.metanmobile.core.ui.stations.StationRowShimmer
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.core.ui.util.isGoogleServicesAvailable
import com.ngapp.metanmobile.feature.stations.state.StationsAction

/** A NativeBanner after every this many stations. */
private const val NATIVE_BANNER_INTERVAL = 8

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
        contentPadding = PaddingValues(bottom = LocalMMFloatingBarPadding.current),
    ) {
        if (stationsList.isNotEmpty()) {
            stationsList.forEachIndexed { index, station ->
                item(key = station.code) {
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
                if (isNativeBannerSlot(index, NATIVE_BANNER_INTERVAL, stationsList.size)) {
                    item(key = "nativeBanner-$index", span = { GridItemSpan(maxLineSpan) }) {
                        NativeBanner(slotKey = "stations-$index", layout = NativeBannerLayout.Station)
                    }
                }
            }
        } else {
            items(10) {
                StationRowShimmer(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
