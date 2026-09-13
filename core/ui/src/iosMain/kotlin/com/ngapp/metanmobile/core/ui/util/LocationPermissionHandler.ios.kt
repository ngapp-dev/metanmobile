/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ngapp.metanmobile.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.darwin.NSObject

private fun CLAuthorizationStatus.isGranted() =
    this == kCLAuthorizationStatusAuthorizedAlways || this == kCLAuthorizationStatusAuthorizedWhenInUse

@Composable
actual fun PermissionsManager(content: @Composable () -> Unit) {
    val permissionsState = remember { PermissionsState() }
    val locationManager = remember { CLLocationManager() }

    DisposableEffect(locationManager) {
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                permissionsState.hasLocationPermissions = manager.authorizationStatus.isGranted()
            }
        }
        locationManager.delegate = delegate
        permissionsState.hasLocationPermissions = locationManager.authorizationStatus.isGranted()
        permissionsState.requestPermissions = {
            if (locationManager.authorizationStatus == kCLAuthorizationStatusNotDetermined) {
                locationManager.requestWhenInUseAuthorization()
            } else {
                openAppSettings()
            }
        }
        onDispose { locationManager.delegate = null }
    }

    CompositionLocalProvider(LocalPermissionsState provides permissionsState) {
        content()
    }
}

actual fun openAppSettings() {
    val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
    UIApplication.sharedApplication.openURL(url)
}

// iOS has no Google-Play-Services-style gate on location; the system permission flow above is
// always usable.
actual fun isGoogleServicesAvailable(): Boolean = true
