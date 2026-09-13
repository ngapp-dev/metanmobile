package com.ngapp.metanmobile.feature.termsandconditions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.ngapp.metanmobile.core.ui.util.slideInLeftComposable
import com.ngapp.metanmobile.feature.termsandconditions.TermsAndConditionsScreen
import kotlinx.serialization.Serializable

fun NavController.navigateToTermsAndConditions(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = TermsAndConditionsNavigation) { navOptions() }
}

fun NavGraphBuilder.termsAndConditionsScreen(onBackClick: () -> Unit) {
    slideInLeftComposable<TermsAndConditionsNavigation> {
        TermsAndConditionsScreen(onBackClick = onBackClick)
    }
}

@Serializable
data object TermsAndConditionsNavigation
