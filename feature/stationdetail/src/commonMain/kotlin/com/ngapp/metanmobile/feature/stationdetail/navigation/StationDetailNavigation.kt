package com.ngapp.metanmobile.feature.stationdetail.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.ngapp.metanmobile.core.ui.util.slideInLeftComposable
import com.ngapp.metanmobile.feature.stationdetail.StationDetailRoute
import kotlinx.serialization.Serializable

private const val DEEP_LINK_URI_PATTERN = "https://metan.by/ecogas-map"

/**
 * Standalone entry point for a station's detail page, used when it's reached independently of
 * the Stations tab — currently only via a deep link (e.g. a station URL tapped from a news
 * article). Deliberately NOT the same route the Stations/Home tabs use to show a station's
 * bottom sheet: a plain top-level push here keeps it out of the bottom nav's
 * `popUpTo(...){ saveState = true }`/`restoreState = true` bookkeeping for those tabs — otherwise
 * it gets swept up into whichever tab's back stack it happened to be sitting on top of, and
 * resurfaces there instead of the actual tab content.
 */
@Serializable
data class StationDetailNavigation(val stationCode: String)

fun NavController.navigateToStationDetail(
    stationCode: String, navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route = StationDetailNavigation(stationCode)) { navOptions() }
}

fun NavGraphBuilder.stationDetailScreen(
    onNewsDetailClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    slideInLeftComposable<StationDetailNavigation>(
        deepLinks = listOf(navDeepLink { uriPattern = "$DEEP_LINK_URI_PATTERN/{stationCode}/" }),
    ) { backStackEntry ->
        val stationCode = backStackEntry.toRoute<StationDetailNavigation>().stationCode
        // No extra toolbar/back-arrow here — StationDetailRoute's own header already has a
        // close (X) button wired to onBackClick, carried over from its bottom-sheet origin.
        // That origin is also why it needs an explicit top status-bar inset here: as a bottom
        // sheet it never reached the top of the screen, so its content was never inset-aware —
        // as a standalone full screen it now has to consume that inset itself.
        StationDetailRoute(
            stationCode = stationCode,
            onNewsDetailClick = onNewsDetailClick,
            onBackClick = onBackClick,
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        )
    }
}
