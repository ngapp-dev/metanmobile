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

package com.ngapp.metanmobile.core.data.repository.location

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.timeIntervalSince1970
import platform.darwin.NSObject
import kotlin.coroutines.resume

// iOS has no Google-Play-Services-style gate on location — CoreLocation is always usable, subject
// only to the ordinary system permission flow (handled separately, see LocationPermissionHandler).
actual fun isPlatformLocationAvailable(): Boolean = true

@OptIn(ExperimentalForeignApi::class)
actual class PlatformLocationSource actual constructor() {
    private val manager = CLLocationManager()

    actual suspend fun getCurrentLocation(): PlatformLocationPoint? =
        suspendCancellableCoroutine { continuation ->
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                    val location = didUpdateLocations.lastOrNull() as? CLLocation
                    val point = location?.coordinate?.useContents {
                        PlatformLocationPoint(
                            latitude = latitude,
                            longitude = longitude,
                            time = (NSDate().timeIntervalSince1970 * 1000).toLong(),
                        )
                    }
                    manager.delegate = null
                    if (continuation.isActive) continuation.resume(point)
                }

                override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                    manager.delegate = null
                    if (continuation.isActive) continuation.resume(null)
                }
            }
            manager.delegate = delegate
            manager.requestLocation()
            continuation.invokeOnCancellation { manager.delegate = null }
        }
}
