package com.ngapp.metanmobile.core.ui.stations

import com.ngapp.metanmobile.core.model.station.UserStationResource

/**
 * Opens the platform's map app(s) at [station]'s location — Android offers a Yandex/Google Maps
 * chooser (mirroring master), iOS opens the system default (Apple Maps, or Google Maps if the
 * user has it set as default) via a `maps:`/`https://maps.google.com` universal link.
 */
expect fun openStationLocation(station: UserStationResource)
