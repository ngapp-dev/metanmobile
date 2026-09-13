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
    // CLLocationManager.delegate is a *weak* Objective-C property (Apple's usual delegate
    // convention) - held only as a local val inside DisposableEffect, this object had no strong
    // Kotlin-side reference anywhere once that block finished running, so Kotlin/Native's own GC
    // was free to collect it before the async callback for the user's permission choice ever
    // arrived (confirmed live: the initial/setup callback fired, but nothing after the user
    // actually responded to the system dialog). remember{} roots it in the composition for as
    // long as PermissionsManager stays composed - i.e. the whole app's lifetime.
    val delegate = remember {
        object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                println("PermissionsManager: locationManagerDidChangeAuthorization -> ${manager.authorizationStatus}")
                permissionsState.hasLocationPermissions = manager.authorizationStatus.isGranted()
            }
        }
    }

    DisposableEffect(locationManager, delegate) {
        locationManager.delegate = delegate
        println("PermissionsManager: initial authorizationStatus = ${locationManager.authorizationStatus}")
        permissionsState.hasLocationPermissions = locationManager.authorizationStatus.isGranted()
        permissionsState.requestPermissions = {
            println("PermissionsManager: requestPermissions() called, current status = ${locationManager.authorizationStatus}")
            if (locationManager.authorizationStatus == kCLAuthorizationStatusNotDetermined) {
                locationManager.requestWhenInUseAuthorization()
            } else {
                openAppSettings()
            }
        }
        onDispose {
            println("PermissionsManager: disposed")
            locationManager.delegate = null
        }
    }

    CompositionLocalProvider(LocalPermissionsState provides permissionsState) {
        content()
    }
}

actual fun openAppSettings() {
    val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
    UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any?>(), completionHandler = null)
}

// iOS has no Google-Play-Services-style gate on location; the system permission flow above is
// always usable.
actual fun isGoogleServicesAvailable(): Boolean = true
