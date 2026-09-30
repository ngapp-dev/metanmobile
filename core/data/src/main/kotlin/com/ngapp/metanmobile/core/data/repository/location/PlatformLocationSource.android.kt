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

import android.annotation.SuppressLint
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks
import com.ngapp.metanmobile.core.common.util.UiAndroidPlatformContextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual fun isPlatformLocationAvailable(): Boolean {
    val context = UiAndroidPlatformContextProvider.context ?: return false
    val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
    return status == ConnectionResult.SUCCESS
}

actual class PlatformLocationSource actual constructor() {
    private val context = requireNotNull(UiAndroidPlatformContextProvider.context)
    private val client by lazy { LocationServices.getFusedLocationProviderClient(context) }

    @SuppressLint("MissingPermission")
    // Callers only reach here after the caller-supplied permission flag (see
    // OfflineFirstLocationsRepository.updateLocation) confirmed the permission is granted.
    actual suspend fun getCurrentLocation(): PlatformLocationPoint? = withContext(Dispatchers.IO) {
        // lastLocation is just a cache — null whenever the device has never computed a fix (fresh
        // device, GPS/network location off). Fall back to one active request instead of silently
        // giving up, so "permission granted" doesn't mean "stuck with no location forever" until
        // something else on the device happens to trigger a fix.
        val cached = runCatching { Tasks.await(client.lastLocation) }.getOrNull()
        val location = cached ?: runCatching {
            Tasks.await(
                client.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token,
                )
            )
        }.getOrNull()
        location?.let { PlatformLocationPoint(it.latitude, it.longitude, it.time) }
    }
}
