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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.ngapp.metanmobile.core.ui.UiAndroidPlatformContextProvider
import kotlinx.coroutines.flow.collectLatest

private const val LOCATION_PERMISSION_PREFS_NAME = "location_permission_prefs"
private const val KEY_HAS_REQUESTED_LOCATION_PERMISSION = "has_requested_location_permission"

@Composable
@OptIn(ExperimentalPermissionsApi::class)
actual fun PermissionsManager(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val permissionsState = remember { PermissionsState() }
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val prefs = remember {
        context.getSharedPreferences(LOCATION_PERMISSION_PREFS_NAME, Context.MODE_PRIVATE)
    }
    fun hasRequestedBefore() = prefs.getBoolean(KEY_HAS_REQUESTED_LOCATION_PERMISSION, false)
    fun markRequested() = prefs.edit().putBoolean(KEY_HAS_REQUESTED_LOCATION_PERMISSION, true).apply()

    LaunchedEffect(locationPermissionsState) {
        snapshotFlow { locationPermissionsState.allPermissionsGranted }
            .collectLatest { allPermissionsGranted ->
                permissionsState.hasLocationPermissions = allPermissionsGranted
            }
    }

    var pendingRequest by remember { mutableStateOf(false) }
    var deniedAfterRequest by remember { mutableStateOf(false) }

    permissionsState.requestPermissions = {
        if (!locationPermissionsState.allPermissionsGranted) {
            val canAskAgain = locationPermissionsState.permissions.any { permission ->
                val status = permission.status
                status is PermissionStatus.Denied && status.shouldShowRationale
            }
            if (shouldShowSystemDialog(hasRequestedBefore(), canAskAgain)) {
                markRequested()
                pendingRequest = true
                locationPermissionsState.launchMultiplePermissionRequest()
            } else {
                openAppSettings()
            }
        }
    }

    LaunchedEffect(locationPermissionsState) {
        snapshotFlow { locationPermissionsState.permissions.map { it.status } }
            .collectLatest {
                if (pendingRequest) {
                    pendingRequest = false
                    if (!locationPermissionsState.allPermissionsGranted) {
                        deniedAfterRequest = true
                    }
                }
            }
    }

    if (deniedAfterRequest) {
        val canAskAgain = locationPermissionsState.permissions.any { permission ->
            val status = permission.status
            status is PermissionStatus.Denied && status.shouldShowRationale
        }
        LocationRationaleDialog(
            canAskAgain = canAskAgain,
            onRetry = {
                deniedAfterRequest = false
                if (canAskAgain) {
                    pendingRequest = true
                    locationPermissionsState.launchMultiplePermissionRequest()
                } else {
                    openAppSettings()
                }
            },
            onDeclineAnyway = { deniedAfterRequest = false },
        )
    }

    CompositionLocalProvider(LocalPermissionsState provides permissionsState) {
        content()
    }
}

actual fun openAppSettings() {
    val context = UiAndroidPlatformContextProvider.context ?: return
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

actual fun isGoogleServicesAvailable(): Boolean {
    val context = UiAndroidPlatformContextProvider.context ?: return false
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
    return resultCode == ConnectionResult.SUCCESS
}
