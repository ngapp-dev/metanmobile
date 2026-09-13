package com.ngapp.metanmobile.feature.favorites.ui

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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.logStationResourceOpened
import com.ngapp.metanmobile.core.ui.stations.FavoriteRow
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.core.ui.util.isGoogleServicesAvailable
import com.ngapp.metanmobile.feature.favorites.state.FavoritesAction

@Composable
internal fun FavoritesContent(
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    favoriteStationsList: List<UserStationResource>,
    onAction: (FavoritesAction) -> Unit,
    onDetailClick: (String) -> Unit,
) {
    val permissionsState = LocalPermissionsState.current
    val uriHandler = LocalUriHandler.current
    val isGoogleServicesAvailable = remember { isGoogleServicesAvailable() }
    val analyticsHelper = LocalAnalyticsHelper.current

    LazyVerticalGrid(
        state = gridState,
        modifier = modifier
            .fillMaxSize()
            .animateContentSize(),
        columns = GridCells.Adaptive(300.dp),
    ) {
        items(items = favoriteStationsList, key = { station -> station.code }) { station ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MMColors.cardBackgroundColor)
            ) {
                FavoriteRow(
                    favoriteStation = station,
                    locationPermissionGranted = permissionsState.hasLocationPermissions,
                    isGoogleServicesAvailable = isGoogleServicesAvailable,
                    onPermissionRequestAgain = { permissionsState.requestPermissions() },
                    onGoogleServicesRequest = {
                        uriHandler.openUri("market://details?id=com.google.android.gms")
                    },
                    onDetailClick = {
                        analyticsHelper.logStationResourceOpened(station.code)
                        onDetailClick(station.code)
                    },
                    onDeleteClick = {
                        onAction(FavoritesAction.UpdateStationForDelete(station))
                        onAction(FavoritesAction.ShowBottomSheet(true))
                    }
                )
            }
        }
    }
}
