/*
 * Copyright 2026 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
