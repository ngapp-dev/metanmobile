package com.ngapp.metanmobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ngapp.metanmobile.composeapp.DeepLinkHolder
import com.ngapp.metanmobile.composeapp.SharedMetanMobileContent
import com.ngapp.metanmobile.core.domain.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.ui.UiAndroidPlatformContextProvider
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/** Android host for the shared Compose Multiplatform application. */
class MainActivity : ComponentActivity() {
    private val userDataRepository: UserDataRepository by inject()

    // Only treat the launch Intent as a deep link to handle on a genuinely fresh start.
    // savedInstanceState is non-null on a recreate() too — e.g. a per-app language switch — and
    // `intent` still holds whatever originally launched the Activity, so without this check every
    // recreate would re-navigate to that same deep link and undo whatever screen the user had
    // since navigated to. Mirrors master's MainActivity.onCreate exactly.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.dataString?.let { DeepLinkHolder.onDeepLink(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate()/setContent(): this is what actually switches the
        // window off Theme.MM.Splash (which — unlike Theme.MM — isn't a NoActionBar theme) once
        // the splash is dismissed. Without it the Activity is stuck on the splash theme forever
        // and shows a native ActionBar with the app label on top of the Compose content.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Belt-and-braces for the same issue: hiding the ActionBar via the API works regardless
        // of how postSplashScreenTheme's windowNoTitle/windowActionBar attributes get resolved,
        // since (unlike requestWindowFeature) it doesn't need to run before the window exists.
        actionBar?.hide()
        UiAndroidPlatformContextProvider.setContext(this)

        if (savedInstanceState == null) {
            intent.dataString?.let { DeepLinkHolder.onDeepLink(it) }
        }

        // Mirrors master's MainActivity: keep the native splash up until userData has loaded at
        // least once, instead of letting Compose draw its first frame (and MetanMobileNavHost
        // lock in a startDestination) before we actually know shouldHideOnboarding. Without this,
        // onboarding either always wins the race on a cold start or briefly flashes before the
        // real destination is known — this closes that window at the OS-splash level, same as
        // master's setKeepOnScreenCondition, so there's no visible flash either way.
        var userDataLoaded = false
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userDataRepository.userData.collect { userDataLoaded = true }
            }
        }
        splashScreen.setKeepOnScreenCondition { !userDataLoaded }

        enableEdgeToEdge()
        setContent { SharedMetanMobileContent() }
    }
}
