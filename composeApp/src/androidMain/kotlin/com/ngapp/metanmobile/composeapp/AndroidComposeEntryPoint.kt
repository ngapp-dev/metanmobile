package com.ngapp.metanmobile.composeapp

import androidx.compose.runtime.Composable

/** Android adapter kept intentionally small while the existing Android navigation is migrated. */
@Composable
fun SharedMetanMobileContent() {
    MetanMobileApp(
        initialOnboarding = true,
    )
}
