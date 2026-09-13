package com.ngapp.metanmobile.core.ui.util

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.ngapp.metanmobile.core.ui.UiAndroidPlatformContextProvider
import java.util.Locale

actual class LanguageHelper actual constructor() {

    private val context = requireNotNull(UiAndroidPlatformContextProvider.context)

    actual fun changeLanguage(languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
    }

    actual fun getLanguageCode(): String {
        val appLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales[0]
        } else {
            AppCompatDelegate.getApplicationLocales()[0]
        }
        return (appLocale ?: Locale.getDefault()).toLanguageTag().split("-").first()
    }
}

actual fun isPerAppLanguageConfigSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
