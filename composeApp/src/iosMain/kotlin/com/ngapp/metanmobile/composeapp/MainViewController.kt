package com.ngapp.metanmobile.composeapp

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = { enforceStrictPlistSanityCheck = false },
) {
    MetanMobileApp(initialOnboarding = true)
}

/** Called from `MetanMobileApp.swift`'s `.onOpenURL` — see [DeepLinkHolder]. */
fun handleDeepLink(url: String) {
    DeepLinkHolder.onDeepLink(url)
}
