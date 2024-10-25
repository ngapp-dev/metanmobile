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

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import com.ngapp.metanmobile.MainActivity
import com.ngapp.metanmobile.core.rules.GrantLocationPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.ngapp.metanmobile.core.designsystem.R as DesignsystemR
import com.ngapp.metanmobile.core.ui.R as CoreUiR

/**
 * Tests all the navigation flows that are handled by the navigation library.
 */
@HiltAndroidTest
class NavigationTest {

    /**
     * Manages the components' state and is used to perform injection on your test
     */
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    /**
     * Grants [ACCESS_COARSE_LOCATION] and [ACCESS_FINE_LOCATION] permissions.
     */
    @get:Rule(order = 1)
    val locationPermissionPermission = GrantLocationPermissionRule()

    /**
     * Use the primary activity to initialize the app normally.
     */
    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // The strings used for matching in these tests
    private val navigateUp by composeTestRule.stringResource(CoreUiR.string.core_ui_navigate_up)
    private val home by composeTestRule.stringResource(DesignsystemR.string.core_designsystem_bottom_menu_home)
    private val stations by composeTestRule.stringResource(DesignsystemR.string.core_designsystem_bottom_menu_stations)
    private val news by composeTestRule.stringResource(DesignsystemR.string.core_designsystem_bottom_menu_news)
    private val favorites by composeTestRule.stringResource(DesignsystemR.string.core_designsystem_bottom_menu_favorites)
    private val menu by composeTestRule.stringResource(DesignsystemR.string.core_designsystem_description_menu_icon)

    @Before
    fun setup() = hiltRule.inject()


    @Test
    fun firstScreen_isHome() {
        composeTestRule.apply {
            // VERIFY for you is selected
            onNodeWithText(home).assertIsSelected()
        }
    }

    /*
     * Top level destinations should never show an up affordance.
     */
    @Test
    fun topLevelDestinations_doNotShowUpArrow() {
        composeTestRule.apply {
            // GIVEN the user is on any of the top level destinations, THEN the Up arrow is not shown.
            onNodeWithContentDescription(navigateUp).assertDoesNotExist()

            onNodeWithText(stations).performClick()
            onNodeWithContentDescription(navigateUp).assertDoesNotExist()

            onNodeWithText(news).performClick()
            onNodeWithContentDescription(navigateUp).assertDoesNotExist()

            onNodeWithText(favorites).performClick()
            onNodeWithContentDescription(navigateUp).assertDoesNotExist()
        }
    }

    @Test
    fun topLevelDestinations_showMenuIcon() {
        composeTestRule.apply {
            onNodeWithContentDescription(menu).assertExists()

            onNodeWithText(stations).performClick()
            onNodeWithContentDescription(navigateUp).assertDoesNotExist()
            onNodeWithContentDescription(menu).assertExists()

            onNodeWithText(news).performClick()
            onNodeWithContentDescription(navigateUp).assertDoesNotExist()
            onNodeWithContentDescription(menu).assertExists()

            onNodeWithText(favorites).performClick()
            onNodeWithContentDescription(navigateUp).assertDoesNotExist()
            onNodeWithContentDescription(menu).assertExists()
        }
    }

    /*
     * There should always be at most one instance of a top-level destination at the same time.
     */
    @Test(expected = NoActivityResumedException::class)
    fun homeDestination_back_quitsApp() {
        composeTestRule.apply {
            // GIVEN the user navigates to the Interests destination
            onNodeWithText(stations).performClick()
            // and then navigates to the For you destination
            onNodeWithText(home).performClick()
            // WHEN the user uses the system button/gesture to go back
            Espresso.pressBack()
            // THEN the app quits
        }
    }

    /*
     * When pressing back from any top level destination except "For you", the app navigates back
     * to the "For you" destination, no matter which destinations you visited in between.
     */
    @Test
    fun navigationBar_backFromAnyDestination_returnsToHome() {
        composeTestRule.apply {
            // GIVEN the user navigated to the Interests destination
            onNodeWithText(stations).performClick()
            // WHEN the user uses the system button/gesture to go back,
            Espresso.pressBack()
            // THEN the app shows the For You destination
            onNodeWithText(home).assertExists()
        }
    }
}
