package com.ngapp.metanmobile.feature.stations.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ngapp.metanmobile.core.model.location.LocationResource
import com.ngapp.metanmobile.core.model.station.UserStationResource

/**
 * The "Map" tab content. Android renders a real Google Map (ported from master); iOS has no maps
 * integration yet (no MapKit interop wired up) and shows a simple "unavailable" placeholder
 * instead — a deliberate, temporary platform gap, not a design choice.
 */
@Composable
expect fun StationMapContent(
    modifier: Modifier = Modifier,
    stationList: List<UserStationResource>,
    userLocation: LocationResource?,
    bottomSheetPartiallyExpanded: Boolean,
    onDetailClick: (String) -> Unit,
)
