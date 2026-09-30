package com.ngapp.metanmobile.composeapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.ngapp.metanmobile.composeapp.navigation.TopLevelDestination
import com.ngapp.metanmobile.feature.careers.navigation.navigateToCareers
import com.ngapp.metanmobile.feature.faq.navigation.navigateToFaq
import com.ngapp.metanmobile.feature.news.navigation.navigateToNewsDetail
import com.ngapp.metanmobile.feature.onboarding.navigation.OnboardingNavigationRoute
import com.ngapp.metanmobile.feature.stationdetail.navigation.navigateToStationDetail

@Composable
fun rememberMetanMobileAppState(navController: NavHostController = rememberNavController()): MetanMobileAppState = remember(navController) { MetanMobileAppState(navController) }

@Stable
class MetanMobileAppState(val navController: NavHostController) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = TopLevelDestination.entries.firstOrNull { currentDestination?.route == it.route }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    fun navigateToTopLevelDestination(destination: TopLevelDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateFromOnboardingToHome() = navController.navigate(TopLevelDestination.HOME.route) {
        popUpTo(OnboardingNavigationRoute) { inclusive = true }
    }

    /**
     * Routes an incoming deep link (an App Link tap on Android, a Universal Link/custom-scheme
     * open on iOS) to the matching screen — the KMP counterpart of master's `MMAppState.
     * navigateToDeepLink(uri: Uri)`. Takes a plain URL string rather than a platform `Uri`/`NSURL`
     * since there's no shared KMP URI type; both platforms' entry points just forward whatever
     * string they were opened with here unparsed.
     *
     * Per-composable `deepLinks = listOf(navDeepLink { ... })` declarations exist on some routes
     * too, but (matching master) they aren't what actually drives this — Compose Navigation
     * doesn't auto-wire an incoming platform Intent/URL to `NavController.handleDeepLink()`, so
     * the two platform entry points (`MainActivity.onNewIntent`/`onCreate`, `.onOpenURL` on iOS)
     * push the raw URL here and this does the actual routing.
     */
    fun navigateToDeepLink(url: String) {
        // Two forms reach here: a real "https://metan.by/..." App/Universal Link, or this app's
        // own "metanmobile://..." custom scheme (used on iOS, which — unlike Android's App
        // Links — can't verify a real https:// link without a server-hosted
        // apple-app-site-association file and an Apple Developer Team entitlement, neither of
        // which this repo alone can set up). Both carry the same path segments after their
        // respective prefixes, just under a different host/scheme, so route them identically.
        val path = when {
            url.startsWith("https://metan.by") -> url.removePrefix("https://metan.by")
            url.startsWith("http://metan.by") -> url.removePrefix("http://metan.by")
            url.startsWith("metanmobile://") -> url.removePrefix("metanmobile://")
            else -> return
        }
        val segments = path.split("/").filter { it.isNotEmpty() }
        val singleTop = navOptions { launchSingleTop = true }
        when (segments.getOrNull(0)) {
            "ecogas-map" -> {
                val stationCode = segments.getOrNull(1)
                if (stationCode != null) {
                    navController.navigateToStationDetail(stationCode) { launchSingleTop = true }
                } else {
                    navController.navigate(TopLevelDestination.STATIONS.route, singleTop)
                }
            }

            "news" -> if (segments.getOrNull(1) == "by") {
                segments.getOrNull(2)?.let {
                    navController.navigateToNewsDetail(newsId = it) { launchSingleTop = true }
                }
            }

            "faq" -> navController.navigateToFaq { launchSingleTop = true }
            "career" -> navController.navigateToCareers { launchSingleTop = true }
            null -> navController.navigate(TopLevelDestination.HOME.route, singleTop)
        }
    }
}
