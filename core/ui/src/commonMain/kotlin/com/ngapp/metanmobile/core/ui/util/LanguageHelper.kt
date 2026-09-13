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

/**
 * Whether the language row should instead open the OS's own per-app language screen (via
 * [openAppSettings]) rather than staying hidden — true only on iOS 13+, where Apple gives apps no
 * in-app language-override API, but does let the user pick a language for this specific app from
 * Settings once the app declares `CFBundleLocalizations` (which it now does). `false` everywhere
 * [isPerAppLanguageConfigSupported] is also false for another reason (pre-13 Android has no
 * per-app language screen to send the user to either), so the row simply stays hidden there,
 * unchanged from before.
 */
expect fun isSystemLanguageSettingsAvailable(): Boolean
