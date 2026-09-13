package com.ngapp.metanmobile.core.ui.util

import androidx.compose.runtime.Composable

/**
 * Intercepts the system back gesture/button while [enabled], invoking [onBack] instead of the
 * default pop behavior. No-op on platforms without a system back concept (iOS).
 */
@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
