package com.ngapp.metanmobile.core.ui.stations

import com.ngapp.metanmobile.core.model.station.UserStationResource
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * iOS has no equivalent of an intent chooser between named map apps — open via Apple Maps' `maps:`
 * scheme, which the system resolves to the user's actual preferred maps app where one is set.
 */
actual fun openStationLocation(station: UserStationResource) {
    // No `q=` title param — station titles can contain spaces/Cyrillic that would need percent-
    // encoding here; the coordinates alone are enough to drop a pin at the right place.
    val url = "https://maps.apple.com/?ll=${station.latitude},${station.longitude}"
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(nsUrl)
}
