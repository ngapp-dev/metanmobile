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

import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ngapp.metanmobile.composeapp.navigation.TopLevelDestination
import com.ngapp.metanmobile.composeapp.ui.MetanMobileAppState
import com.ngapp.metanmobile.composeapp.ui.rememberMetanMobileAppState
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests [MetanMobileAppState].
 *
 * This is a much smaller surface than master's `MMAppState`/`MMAppStateTest`: the KMP restoration
 * moved network/timezone monitoring out of app state entirely (nothing in `composeApp` reads
 * `NetworkMonitor`/`TimeZoneMonitor` through this class the way `MainActivity`'s `rememberMMAppState`
 * did) — so master's `mmAppState_whenNetworkMonitorIsOffline_StateIsOffline` and
 * `mmAppState_differentTZ_withTimeZoneMonitorChange` have no equivalent to port; there is no
 * `isOffline`/`currentTimeZone` on [MetanMobileAppState] to test. What's covered below
 * (`currentDestination` tracking, `topLevelDestinations`) is the part of the old test that still
 * has a direct analog.
 */
class MetanMobileAppStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Subject under test.
    private lateinit var state: MetanMobileAppState

    @Test
    fun currentDestination_updatesAsNavControllerNavigates() {
        var currentDestinationRoute: String? = null

        composeTestRule.setContent {
            val navController = rememberNavController()
            state = remember(navController) { MetanMobileAppState(navController) }

            // Read currentDestination every recomposition so it tracks navigation below.
            currentDestinationRoute = state.currentDestination?.route

            NavHost(navController = navController, startDestination = "a") {
                composable("a") { }
                composable("b") { }
            }
        }

        composeTestRule.runOnIdle {
            state.navController.navigate("b")
        }
        composeTestRule.waitForIdle()

        assertEquals("b", currentDestinationRoute)
    }

    @Test
    fun topLevelDestinations_matchTheFourBottomTabs() {
        composeTestRule.setContent {
            state = rememberMetanMobileAppState()
        }

        assertEquals(4, state.topLevelDestinations.size)
        assertTrue(state.topLevelDestinations.any { it == TopLevelDestination.HOME })
        assertTrue(state.topLevelDestinations.any { it == TopLevelDestination.STATIONS })
        assertTrue(state.topLevelDestinations.any { it == TopLevelDestination.NEWS })
        assertTrue(state.topLevelDestinations.any { it == TopLevelDestination.FAVORITES })
    }
}
