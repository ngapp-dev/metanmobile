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

package com.ngapp.metanmobile.feature.onboarding

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.core.ui.util.PermissionsState
import com.ngapp.metanmobile.feature.onboarding.state.OnboardingAction
import com.ngapp.metanmobile.feature.onboarding.state.OnboardingUiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import com.ngapp.metanmobile.core.designsystem.R as DesignSystemR

/**
 * UI tests for [OnboardingScreen] - pager navigation (Next/Back/Skip) and the location
 * permission request that's supposed to fire exactly once, the first time the user reaches the
 * third page ("find stations near you"), regardless of how they got there.
 *
 * [OnboardingScreen] is exercised directly (not through [OnboardingRoute]/Hilt/navigation) with
 * a caller-supplied [OnboardingUiState] and a fake [PermissionsState] - it needs neither, so
 * driving the whole app through [com.ngapp.metanmobile.MainActivity] would only make this slower
 * and more indirect for no extra coverage.
 */
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val actions = mutableListOf<OnboardingAction>()
    private val permissionsState = PermissionsState()
    private var permissionRequestCount = 0

    private val newsTitle by lazy {
        composeTestRule.activity.getString(R.string.feature_onboarding_title_news)
    }
    private val stationsTitle by lazy {
        composeTestRule.activity.getString(R.string.feature_onboarding_title_stations)
    }
    private val favoritesTitle by lazy {
        composeTestRule.activity.getString(R.string.feature_onboarding_title_favorites)
    }
    private val getStartedText by lazy {
        composeTestRule.activity.getString(R.string.feature_onboarding_button_get_start)
    }
    private val skipText by lazy {
        composeTestRule.activity.getString(DesignSystemR.string.core_designsystem_onboarding_button_skip)
    }

    @Before
    fun setUp() {
        permissionsState.requestPermissions = { permissionRequestCount++ }
        composeTestRule.setContent {
            MMTheme {
                CompositionLocalProvider(LocalPermissionsState provides permissionsState) {
                    OnboardingScreen(
                        modifier = Modifier,
                        uiState = OnboardingUiState.Shown,
                        onAction = { actions += it },
                    )
                }
            }
        }
    }

    @Test
    fun firstPage_showsNewsPage_withNoBackButton() {
        composeTestRule.onNodeWithText(newsTitle).assertExists()
        composeTestRule.onNodeWithContentDescription("Back").assertDoesNotExist()
    }

    @Test
    fun clickingNext_advancesThroughPagesInOrder() {
        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.onNodeWithText(stationsTitle).assertExists()

        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.onNodeWithText(favoritesTitle).assertExists()
    }

    @Test
    fun backButton_appearsAfterFirstPage_andReturnsOnePage() {
        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.onNodeWithContentDescription("Back").assertExists()

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText(newsTitle).assertExists()
        composeTestRule.onNodeWithContentDescription("Back").assertDoesNotExist()
    }

    @Test
    fun lastPage_showsGetStartedButton_whichDismissesOnboardingWhenClicked() {
        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.onNodeWithText(getStartedText).assertExists()

        composeTestRule.onNodeWithContentDescription("Next").performClick()

        assertEquals(OnboardingAction.DismissOnboarding, actions.single())
    }

    @Test
    fun locationPermission_isNotRequestedBeforeReachingTheLastPage() {
        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.waitForIdle()

        assertEquals(0, permissionRequestCount)
    }

    @Test
    fun locationPermission_isRequestedExactlyOnceOnReachingTheLastPage_evenAfterRevisitingIt() {
        // Each click launches its page change in its own coroutine (rememberCoroutineScope) -
        // without an explicit wait, a second performClick() can fire before the first one's
        // page change (and the LaunchedEffect it triggers) has actually settled.
        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.waitForIdle()
        assertEquals(1, permissionRequestCount)

        // Leaving and coming back to the last page must not ask again.
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.waitForIdle()
        assertEquals(1, permissionRequestCount)
    }

    @Test
    fun skip_jumpsStraightToTheLastPage_andRequestsLocationPermission() {
        composeTestRule.onNodeWithText(skipText).performClick()

        composeTestRule.onNodeWithText(favoritesTitle).assertExists()
        assertEquals(1, permissionRequestCount)
    }
}
