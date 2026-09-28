@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.ngapp.metanmobile.composeapp

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.theme.shouldUseDarkTheme
import com.ngapp.metanmobile.core.domain.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.feature.onboarding.OnboardingViewModel
import org.koin.compose.koinInject
import com.ngapp.metanmobile.composeapp.navigation.MetanMobileNavHost
import com.ngapp.metanmobile.composeapp.ui.rememberMetanMobileAppState
import com.ngapp.metanmobile.core.ui.util.PermissionsManager

/** Shared Compose entry point used by Android and the iOS host application. */
@Composable
fun MetanMobileApp(
    initialOnboarding: Boolean = true,
    userDataRepository: UserDataRepository = koinInject(),
    onboardingViewModel: OnboardingViewModel = koinInject(),
    // Constructing this via koinInject() is enough to kick off its init{} block (first-run
    // homeReorderableList seeding, etc.) — nothing here needs to read from it directly.
    mainViewModel: MainViewModel = koinInject(),
) {
    val userData = userDataRepository.userData.collectAsStateWithLifecycle(initialValue = null).value
    val darkThemeConfig = userData?.darkThemeConfig ?: DarkThemeConfig.FOLLOW_SYSTEM
    SystemBarsAppearance(darkTheme = shouldUseDarkTheme(darkThemeConfig))
    MetanMobileTheme(darkThemeConfig) {
        PermissionsManager {
            MetanMobileNavHost(
                initialOnboarding = initialOnboarding,
                appState = rememberMetanMobileAppState(),
                onboardingViewModel = onboardingViewModel,
            )
        }
    }
}
