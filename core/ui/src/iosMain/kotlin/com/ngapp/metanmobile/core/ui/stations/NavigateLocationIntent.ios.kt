package com.ngapp.metanmobile.core.ui.stations

import com.ngapp.metanmobile.core.model.station.UserStationResource
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.UIKit.UIApplication

/**
 * iOS has no equivalent of an intent chooser between named map apps — open via Apple Maps' web
 * URL scheme, which the system resolves to the user's actual preferred maps app where one is set.
 *
 * Uses `daddr` (destination address) with `dirflg=d`, matching Google Maps' `daddr=`-driven
 * behavior on Android: this lands directly on Apple Maps' route screen (current location ->
 * destination, "Go" one tap away), not just a centered pin. Passing `ll=lat,long` *alongside*
 * `daddr` was tried first and made Maps fall back to "look at this point" instead of building a
 * route at all - `daddr` alone is what actually triggers directions mode.
 *
 * The station's real address is used rather than bare coordinates because Android can drop a
 * precisely-labeled pin via `googleMapsTag`, tying it to an actual place in Google's own database
 * - Apple Maps has no equivalent tag for these stations, so geocoding the address text is the only
 * way to land on a correctly-labeled destination instead of routing to an unlabeled field.
 */
actual fun openStationLocation(station: UserStationResource) {
    val destination = station.address.ifBlank { "${station.latitude},${station.longitude}" }
    val components = NSURLComponents(string = "https://maps.apple.com/")
    components.queryItems = listOf(
        NSURLQueryItem(name = "daddr", value = destination),
        NSURLQueryItem(name = "dirflg", value = "d"),
    )
    val nsUrl = components.URL ?: return
    UIApplication.sharedApplication.openURL(nsUrl, options = emptyMap<Any?, Any?>(), completionHandler = null)
}
