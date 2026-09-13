package com.ngapp.metanmobile.core.ui.util

import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

// iOS has no per-app language override API equivalent to Android's LocaleManager, and
// isPerAppLanguageConfigSupported() below keeps the language row/dialog out of the UI entirely —
// this class is never actually exercised, just needed to satisfy the expect declaration.
actual class LanguageHelper actual constructor() {
    actual fun changeLanguage(languageCode: String) {
        // No-op: unreachable, see isPerAppLanguageConfigSupported().
    }

    actual fun getLanguageCode(): String =
        (NSLocale.preferredLanguages.firstOrNull() as? String)?.split("-")?.first() ?: "en"
}

actual fun isPerAppLanguageConfigSupported(): Boolean = false
