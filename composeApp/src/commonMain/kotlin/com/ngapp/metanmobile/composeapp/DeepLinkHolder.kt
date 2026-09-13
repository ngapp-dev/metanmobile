package com.ngapp.metanmobile.composeapp

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Cross-platform hand-off point for an incoming deep link URL: `MainActivity`
 * (`onCreate`/`onNewIntent`) pushes into it on Android, `MetanMobileApp.swift`'s `.onOpenURL`
 * pushes into it on iOS — [MetanMobileNavHost][com.ngapp.metanmobile.composeapp.navigation.
 * MetanMobileNavHost] observes it and forwards to [com.ngapp.metanmobile.composeapp.ui.
 * MetanMobileAppState.navigateToDeepLink]. A plain object rather than a constructor
 * parameter/Koin binding since both platform entry points need to reach it before (Android) or
 * entirely outside (iOS's SwiftUI `View` modifier) the Compose tree that owns the actual
 * `NavController`.
 */
object DeepLinkHolder {
    private val _pendingUrl = MutableStateFlow<String?>(null)
    val pendingUrl = _pendingUrl.asStateFlow()

    fun onDeepLink(url: String) {
        _pendingUrl.value = url
    }

    /** Called once the pending link has been routed, so the same link isn't replayed. */
    fun consume() {
        _pendingUrl.value = null
    }
}
