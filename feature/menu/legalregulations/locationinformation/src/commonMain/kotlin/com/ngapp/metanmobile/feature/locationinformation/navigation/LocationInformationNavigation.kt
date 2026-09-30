package com.ngapp.metanmobile.feature.locationinformation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.ngapp.metanmobile.core.ui.util.slideInLeftComposable
import com.ngapp.metanmobile.feature.locationinformation.LocationInformationScreen
import kotlinx.serialization.Serializable

fun NavController.navigateToLocationInformation(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = LocationInformationNavigation) { navOptions() }
}

fun NavGraphBuilder.locationInformationScreen(onBackClick: () -> Unit) {
    slideInLeftComposable<LocationInformationNavigation> {
        LocationInformationScreen(onBackClick = onBackClick)
    }
}

@Serializable
data object LocationInformationNavigation
