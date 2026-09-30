package com.ngapp.metanmobile.feature.privacypolicy.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.ngapp.metanmobile.core.ui.util.slideInLeftComposable
import com.ngapp.metanmobile.feature.privacypolicy.PrivacyPolicyRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToPrivacyPolicy(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = PrivacyPolicyNavigation) { navOptions() }
}

fun NavGraphBuilder.privacyPolicyScreen(onBackClick: () -> Unit) {
    slideInLeftComposable<PrivacyPolicyNavigation> {
        PrivacyPolicyRoute(onBackClick = onBackClick)
    }
}

@Serializable
data object PrivacyPolicyNavigation
