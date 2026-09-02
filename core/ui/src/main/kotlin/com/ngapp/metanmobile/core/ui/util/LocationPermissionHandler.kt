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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.ui.R
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

    // No custom UI before the very first ask — go straight to the system dialog, that's the
    // familiar/expected flow and adds friction for nothing. Only explain (and offer another
    // try, or Settings) *after* an actual decline — that's also the one moment Android itself
    // tells us whether asking again is worth it (shouldShowRationale).
    var pendingRequest by remember { mutableStateOf(false) }
    var deniedAfterRequest by remember { mutableStateOf(false) }

    permissionsState.requestPermissions = {
        if (!locationPermissionsState.allPermissionsGranted) {
            pendingRequest = true
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    // allPermissionsGranted alone won't fire this on a denial (false -> false, no change), so
    // watch the actual per-permission statuses instead — those do change once the system dialog
    // is answered, granted or not.
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
                    // "Don't ask again" / permanently denied — the system dialog won't come back,
                    // Settings is the only remaining path.
                    openAppSettings(context)
                }
            },
            onDeclineAnyway = { deniedAfterRequest = false },
        )
    }

    CompositionLocalProvider(LocalPermissionsState provides permissionsState) {
        content()
    }
}

@Composable
private fun LocationRationaleDialog(
    canAskAgain: Boolean,
    onRetry: () -> Unit,
    onDeclineAnyway: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDeclineAnyway,
        title = {
            Text(
                text = stringResource(R.string.core_ui_title_location_rationale),
                style = MMTypography.displayMedium,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.core_ui_text_location_rationale),
                style = MMTypography.bodyLarge,
            )
        },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(
                    text = stringResource(
                        if (canAskAgain) {
                            R.string.core_ui_button_permission_request
                        } else {
                            R.string.core_ui_button_open_settings
                        }
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDeclineAnyway) {
                Text(text = stringResource(R.string.core_ui_button_decline_anyway))
            }
        },
    )
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