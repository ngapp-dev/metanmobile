/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tracing.trace
import com.ngapp.metanmobile.core.data.repository.news.NewsResourceQuery
import com.ngapp.metanmobile.core.data.repository.news.UserNewsResourceRepository
import com.ngapp.metanmobile.core.data.util.NetworkMonitor
import com.ngapp.metanmobile.core.data.util.TimeZoneMonitor
import com.ngapp.metanmobile.core.ui.TrackDisposableJank
import com.ngapp.metanmobile.feature.careers.navigation.navigateToCareers
import com.ngapp.metanmobile.feature.faq.navigation.navigateToFaq
import com.ngapp.metanmobile.feature.favorites.navigation.FavoritesNavigation
import com.ngapp.metanmobile.feature.favorites.navigation.navigateToFavorites
import com.ngapp.metanmobile.feature.home.navigation.HomeScreenNavigation
import com.ngapp.metanmobile.feature.home.navigation.navigateToHomeScreen
import com.ngapp.metanmobile.feature.news.detail.navigation.navigateToNewsDetail
import com.ngapp.metanmobile.feature.news.list.navigation.NewsNavigation
import com.ngapp.metanmobile.feature.news.list.navigation.navigateToNews
import com.ngapp.metanmobile.feature.onboarding.navigation.OnboardingScreenNavigation
import com.ngapp.metanmobile.feature.stationdetail.navigation.navigateToStationDetail
import com.ngapp.metanmobile.feature.stations.navigation.StationsNavigation
import com.ngapp.metanmobile.feature.stations.navigation.navigateToStations
import com.ngapp.metanmobile.navigation.TopLevelDestination
import com.ngapp.metanmobile.navigation.TopLevelDestination.FAVORITES
import com.ngapp.metanmobile.navigation.TopLevelDestination.HOME
import com.ngapp.metanmobile.navigation.TopLevelDestination.NEWS
import com.ngapp.metanmobile.navigation.TopLevelDestination.STATIONS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone

@Composable
fun rememberMMAppState(
    networkMonitor: NetworkMonitor,
    userNewsResourceRepository: UserNewsResourceRepository,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): MMAppState {
    NavigationTrackingSideEffect(navController)
    return remember(
        navController,
        coroutineScope,
        networkMonitor,
        userNewsResourceRepository,
        timeZoneMonitor,
    ) {
        MMAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
            userNewsResourceRepository = userNewsResourceRepository,
            timeZoneMonitor = timeZoneMonitor,
        )
    }
}

@Stable
class MMAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    userNewsResourceRepository: UserNewsResourceRepository,
    timeZoneMonitor: TimeZoneMonitor,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            // Collect the currentBackStackEntryFlow as a state
            val currentEntry = navController.currentBackStackEntryFlow.collectAsState(null)

            // Fallback to previousDestination if currentEntry is null
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            with(currentDestination) {
                if (this?.hasRoute<HomeScreenNavigation>() == true) return HOME
                if (this?.hasRoute<StationsNavigation>() == true) return STATIONS
                if (this?.hasRoute<NewsNavigation>() == true) return NEWS
                if (this?.hasRoute<FavoritesNavigation>() == true) return FAVORITES
            }
            return null
        }

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = WhileSubscribed(5_000),
            initialValue = false,
        )

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    /**
     * The top level destinations that have unread news resources.
     */
    val topLevelDestinationsWithUnreadResources: StateFlow<Set<TopLevelDestination>> =
        userNewsResourceRepository.observeAll(query = NewsResourceQuery()).map { userNewsResource ->
            setOfNotNull(NEWS.takeIf { userNewsResource.any { !it.hasBeenViewed } })
        }
            .stateIn(
                coroutineScope,
                WhileSubscribed(5_000),
                initialValue = emptySet(),
            )

    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            coroutineScope,
            WhileSubscribed(5_000),
            TimeZone.currentSystemDefault(),
        )

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }

            when (topLevelDestination) {
                HOME -> navController.navigateToHomeScreen(topLevelNavOptions)
                STATIONS -> navController.navigateToStations(topLevelNavOptions)
                NEWS -> navController.navigateToNews(topLevelNavOptions)
                FAVORITES -> navController.navigateToFavorites(topLevelNavOptions)
            }
        }
    }

    /**
     * Routes an incoming metan.by URL (an App Link tap — from a news article's text, an external
     * app, or the browser) to the matching screen.
     *
     * Deliberately a plain [NavController.navigate] push onto whatever the back stack already is,
     * NOT [NavController.handleDeepLink] — that replaces the entire back stack with a synthetic
     * one rebuilt from the graph's start destination, which loses wherever the user actually was
     * (e.g. a news article) and conflicts with the bottom nav's `restoreState`/`saveState` tabs.
     */
    fun navigateToDeepLink(uri: Uri) {
        if (uri.host != "metan.by") return
        val segments = uri.pathSegments
        val singleTop = navOptions { launchSingleTop = true }
        when (segments.getOrNull(0)) {
            "ecogas-map" -> {
                val stationCode = segments.getOrNull(1)
                if (stationCode != null) {
                    navController.navigateToStationDetail(stationCode) { launchSingleTop = true }
                } else {
                    navController.navigateToStations(singleTop)
                }
            }

            "news" -> if (segments.getOrNull(1) == "by") {
                segments.getOrNull(2)?.let {
                    navController.navigateToNewsDetail(newsId = it) { launchSingleTop = true }
                }
            }

            "faq" -> navController.navigateToFaq { launchSingleTop = true }
            "career" -> navController.navigateToCareers { launchSingleTop = true }
            null -> navController.navigateToHomeScreen(singleTop)
        }
    }

    fun navigateToNewsListFromHomeScreen() = navController.navigate(NewsNavigation) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }

    fun navigateFromOnboardingToHomeScreen() = navController.navigate(HomeScreenNavigation) {
        popUpTo(OnboardingScreenNavigation) { inclusive = true }
    }
}

/**
 * Stores information about navigation events to be used with JankStats
 */
@Composable
private fun NavigationTrackingSideEffect(navController: NavHostController) {
    TrackDisposableJank(navController) { metricsHolder ->
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            metricsHolder.state?.putState("Navigation", destination.route.toString())
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}
