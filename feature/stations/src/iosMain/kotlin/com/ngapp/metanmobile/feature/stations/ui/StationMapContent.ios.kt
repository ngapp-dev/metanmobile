package com.ngapp.metanmobile.feature.stations.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.model.location.LocationResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import dev.icerock.moko.resources.compose.stringResource

/**
 * No MapKit interop wired up yet on iOS — a real map view here would need a `UIKitView` wrapping
 * `MKMapView` with its own camera/marker plumbing (a project of its own, not a Compose-code port
 * like the rest of this screen). Placeholder until that's built.
 */
@Composable
actual fun StationMapContent(
    modifier: Modifier,
    stationList: List<UserStationResource>,
    userLocation: LocationResource?,
    bottomSheetPartiallyExpanded: Boolean,
    onDetailClick: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(imageVector = MMIcons.LocationDisabled, contentDescription = null, tint = Gray400)
        Text(
            text = stringResource(SharedRes.strings.feature_stations_text_map_unavailable_ios),
            style = MMTypography.bodyLarge,
            color = Gray400,
            textAlign = TextAlign.Center,
        )
    }
}
