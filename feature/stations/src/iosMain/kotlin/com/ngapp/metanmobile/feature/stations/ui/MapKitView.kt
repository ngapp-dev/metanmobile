package com.ngapp.metanmobile.feature.stations.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.toolbarIconColor
import com.ngapp.metanmobile.core.model.station.StationType
import com.ngapp.metanmobile.core.model.station.UserStationResource
import dev.icerock.moko.resources.ImageResource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKPointAnnotation
import platform.darwin.NSObject

/**
 * The iOS counterpart of Android's `GoogleMapView` — an `MKMapView` wrapped for Compose via
 * `UIKitView`, since Compose Multiplatform has no built-in map composable (unlike Google Maps
 * Compose on Android). Kept as close to `GoogleMapView`'s behavior/layout as MapKit allows: same
 * marker-icon-per-[StationType] rule, same "my location" FAB overlay (MapKit has no equivalent
 * built in), same tap-to-select-and-recenter-above-the-bottom-sheet flow.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
internal fun MapKitView(
    modifier: Modifier = Modifier,
    mapItems: List<UserStationResource>?,
    center: Pair<Double, Double>,
    bottomSheetPartiallyExpanded: Boolean,
    locationPermissionGranted: Boolean,
    onDetailClick: (code: String, latitude: String, longitude: String) -> Unit,
    onRequirePermissions: () -> Unit = {},
    onMyLocationClick: () -> Unit = {},
) {
    var mapView by remember { mutableStateOf<MKMapView?>(null) }
    val delegate = remember { StationMapDelegate(onAnnotationSelected = onDetailClick) }

    // Recenters on `center`, shifting the visible middle south by a quarter of the zoom span
    // while the bottom sheet covers the bottom of the screen, so a selected station doesn't end
    // up hidden behind it — done as plain coordinate-space arithmetic against the *span we're
    // about to set*, not by round-tripping the target through the map's *current* on-screen
    // pixel projection (convertCoordinate/convertPoint): that round trip only stays accurate for
    // points already near the map's current visible region, and silently produced a nonsense
    // coordinate the moment `center` jumped somewhere far from it — e.g. the very first time this
    // recenters from the Minsk fallback to the user's real (and possibly far away) location.
    LaunchedEffect(mapView, center, bottomSheetPartiallyExpanded) {
        val map = mapView ?: return@LaunchedEffect
        val (latitude, longitude) = center
        val span = MKCoordinateSpanMake(RECENTER_SPAN_DEGREES, RECENTER_SPAN_DEGREES)
        val latitudeOffset = if (bottomSheetPartiallyExpanded) RECENTER_SPAN_DEGREES * 0.25 else 0.0
        val adjustedCoordinate = CLLocationCoordinate2DMake(latitude - latitudeOffset, longitude)
        map.setRegion(MKCoordinateRegionMake(adjustedCoordinate, span), animated = true)
    }

    Box(modifier.fillMaxSize()) {
        UIKitView(
            factory = {
                MKMapView().also { map ->
                    map.delegate = delegate
                    map.setRegion(
                        MKCoordinateRegionMake(
                            CLLocationCoordinate2DMake(center.first, center.second),
                            MKCoordinateSpanMake(0.5, 0.5),
                        ),
                        animated = false,
                    )
                    mapView = map
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { map ->
                map.showsUserLocation = locationPermissionGranted

                val currentAnnotations = map.annotations
                    .filterIsInstance<StationAnnotation>()
                    .associateBy { it.code }
                val newCodes = mapItems.orEmpty().map { it.code }.toSet()

                // Remove stale annotations (station no longer present) and add missing ones —
                // wholesale remove-then-add-all on every recomposition would work too, but drops
                // MapKit's own annotation-view reuse/animation for the ones that didn't change.
                currentAnnotations.filterKeys { it !in newCodes }.values.forEach { map.removeAnnotation(it) }
                mapItems.orEmpty().forEach { station ->
                    if (station.code !in currentAnnotations.keys) {
                        val lat = station.latitude.toDoubleOrNull()
                        val long = station.longitude.toDoubleOrNull()
                        if (lat != null && long != null) {
                            map.addAnnotation(
                                StationAnnotation(
                                    latitude = lat,
                                    longitude = long,
                                    code = station.code,
                                    stationTitle = station.title,
                                    iconResource = station.markerIcon(),
                                ),
                            )
                        }
                    }
                }
            },
        )
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
            contentColor = MaterialTheme.colorScheme.onSecondary,
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

/** Mirrors `GoogleMapView`'s per-[StationType] icon `when`, one-to-one. */
private fun UserStationResource.markerIcon(): ImageResource = when {
    type == StationType.CLFS.typeName && isOperate == 1 -> MMIcons.StationClfs
    type == StationType.CNG.typeName && isOperate == 1 -> MMIcons.StationCng
    type == StationType.SERVICE.typeName && isOperate == 1 -> MMIcons.StationService
    else -> MMIcons.StationNotWorking
}

@OptIn(ExperimentalForeignApi::class)
private class StationAnnotation(
    latitude: Double,
    longitude: Double,
    val code: String,
    stationTitle: String,
    val iconResource: ImageResource,
) : MKPointAnnotation() {
    init {
        setCoordinate(CLLocationCoordinate2DMake(latitude, longitude))
        setTitle(stationTitle)
    }
}

private const val STATION_ANNOTATION_REUSE_ID = "station_marker"

// ~2km-wide view when recentering on a station or the user's location — roughly Android's
// zoom=15 (CameraPosition(location, 15f, ...) in StationMapContent.android.kt), a normal
// "street level" zoom, as opposed to the wide 0.5°-span overview `factory` starts the map at.
private const val RECENTER_SPAN_DEGREES = 0.02

@OptIn(ExperimentalForeignApi::class)
private class StationMapDelegate(
    private val onAnnotationSelected: (code: String, latitude: String, longitude: String) -> Unit,
) : NSObject(), MKMapViewDelegateProtocol {

    override fun mapView(mapView: MKMapView, viewForAnnotation: MKAnnotationProtocol): MKAnnotationView? {
        val annotation = viewForAnnotation as? StationAnnotation ?: return null
        val view = mapView.dequeueReusableAnnotationViewWithIdentifier(STATION_ANNOTATION_REUSE_ID)
            ?: MKAnnotationView(annotation = annotation, reuseIdentifier = STATION_ANNOTATION_REUSE_ID)
        view.annotation = annotation
        view.image = annotation.iconResource.toUIImage()
        view.canShowCallout = true
        return view
    }

    override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
        val annotation = didSelectAnnotationView.annotation as? StationAnnotation ?: return
        annotation.coordinate.useContents {
            onAnnotationSelected(annotation.code, latitude.toString(), longitude.toString())
        }
    }
}
