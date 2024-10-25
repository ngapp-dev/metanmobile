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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.flow.collectLatest

class PermissionsState {
    var hasLocationPermissions by mutableStateOf(false)
    var requestPermissions: () -> Unit = {}
}

val LocalPermissionsState = compositionLocalOf { PermissionsState() }

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun PermissionsManager(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val permissionsState = remember { PermissionsState() }
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(locationPermissionsState) {
        snapshotFlow { locationPermissionsState.allPermissionsGranted }
            .collectLatest { allPermissionsGranted ->
                permissionsState.hasLocationPermissions = allPermissionsGranted
            }
    }

    LaunchedEffect(locationPermissionsState.permissions) {
        locationPermissionsState.permissions.forEach { permissionState ->
            handlePermissionRequest(permissionState, context)
        }
    }

    permissionsState.requestPermissions = {
        locationPermissionsState.permissions.forEach {
            locationPermissionsState.launchMultiplePermissionRequest()
            val status = it.status
            if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
                openAppSettings(context)
            } else {
                locationPermissionsState.launchMultiplePermissionRequest()
            }
        }
    }

    CompositionLocalProvider(LocalPermissionsState provides permissionsState) {
        content()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun handlePermissionRequest(permissionState: PermissionState, context: Context) {
    if (permissionState.status is PermissionStatus.Denied &&
        !(permissionState.status as PermissionStatus.Denied).shouldShowRationale
    ) {
        permissionState.launchPermissionRequest()
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

fun isGoogleServicesAvailable(context: Context): Boolean {
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
    return resultCode == ConnectionResult.SUCCESS
}