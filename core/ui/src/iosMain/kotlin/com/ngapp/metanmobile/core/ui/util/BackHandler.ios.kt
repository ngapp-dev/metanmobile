package com.ngapp.metanmobile.core.ui.util

import androidx.compose.runtime.Composable

/** iOS has no hardware/system back gesture concept equivalent to Android's predictive back. */
@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op on iOS.
}
