package com.ngapp.metanmobile.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import com.ngapp.metanmobile.core.ui.util.slideInLeftComposable
import com.ngapp.metanmobile.feature.onboarding.OnboardingRoute
import kotlinx.serialization.Serializable

fun NavGraphBuilder.onboardingScreen(onSkipOnboarding: () -> Unit) {
    slideInLeftComposable<OnboardingNavigationRoute> {
        OnboardingRoute(onSkipOnboarding = onSkipOnboarding)
    }
}

@Serializable
data object OnboardingNavigationRoute
