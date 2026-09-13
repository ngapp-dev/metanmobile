package com.ngapp.metanmobile.core.ui.util

/**
 * Per-app language override. Android exposes this from API 33 (Tiramisu) via `LocaleManager`
 * (falling back to `AppCompatDelegate` on older API levels internally); iOS has no equivalent
 * system feature, so [isPerAppLanguageConfigSupported] is `false` there and the language row/
 * dialog stay hidden — matching master's own API-level gate on the UI, just extended to "no
 * platform support" rather than "old Android version".
 */
expect class LanguageHelper() {
    fun changeLanguage(languageCode: String)

    /**
     * The language actually in effect: the explicit per-app override once the user has picked
     * one, otherwise the device's current locale.
     */
    fun getLanguageCode(): String
}

expect fun isPerAppLanguageConfigSupported(): Boolean
