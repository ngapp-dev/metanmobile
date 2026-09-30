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

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import dev.icerock.moko.resources.compose.stringResource

class PermissionsState {
    var hasLocationPermissions by mutableStateOf(false)
    var requestPermissions: () -> Unit = {}
}

val LocalPermissionsState = compositionLocalOf { PermissionsState() }

/**
 * Whether asking the platform for the permission again is actually worth it, as opposed to
 * sending the user to Settings directly (permanently-denied case).
 */
internal fun shouldShowSystemDialog(hasRequestedBefore: Boolean, canAskAgain: Boolean): Boolean =
    !hasRequestedBefore || canAskAgain

/** Wraps [content] with location-permission state, exposed via [LocalPermissionsState]. */
@Composable
expect fun PermissionsManager(content: @Composable () -> Unit)

/** Opens the platform's app-settings screen (used once a permission is permanently denied). */
expect fun openAppSettings()

/** Whether the platform's location services stack (Google Play Services on Android) is usable. */
expect fun isGoogleServicesAvailable(): Boolean

@Composable
internal fun LocationRationaleDialog(
    canAskAgain: Boolean,
    onRetry: () -> Unit,
    onDeclineAnyway: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDeclineAnyway,
        title = {
            Text(
                text = stringResource(SharedRes.strings.core_ui_title_location_rationale),
                style = MMTypography.displayMedium,
            )
        },
        text = {
            Text(
                text = stringResource(SharedRes.strings.core_ui_text_location_rationale),
                style = MMTypography.bodyLarge,
            )
        },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(
                    text = stringResource(
                        if (canAskAgain) {
                            SharedRes.strings.core_ui_button_permission_request
                        } else {
                            SharedRes.strings.core_ui_button_open_settings
                        }
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDeclineAnyway) {
                Text(text = stringResource(SharedRes.strings.core_ui_button_decline_anyway))
            }
        },
    )
}
