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

package com.ngapp.metanmobile.core.designsystem.component

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

private const val adsTag = "ads"
private const val contentTag = "content"

/**
 * The ad banner slot (`adsContent`) lives in the same bottom strip as the navigation bar, so it
 * must only ever be composed alongside it - see [MMApp][com.ngapp.metanmobile.ui.MMApp], which
 * only wants the ad to appear on top-level destinations that actually show a bottom bar.
 */
class MMNavigationSuiteScaffoldTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun adsContent_isShown_whenBottomBarIsShown() {
        composeTestRule.setContent {
            MMNavigationSuiteScaffold(
                navigationSuiteItems = {},
                showBottomBar = true,
                adsContent = { Text("Ad", modifier = Modifier.testTag(adsTag)) },
                content = { Text("Content", modifier = Modifier.testTag(contentTag)) },
            )
        }

        composeTestRule.onNodeWithTag(adsTag).assertExists()
        composeTestRule.onNodeWithTag(contentTag).assertExists()
    }

    @Test
    fun adsContent_isNotComposed_whenBottomBarIsHidden() {
        composeTestRule.setContent {
            MMNavigationSuiteScaffold(
                navigationSuiteItems = {},
                showBottomBar = false,
                adsContent = { Text("Ad", modifier = Modifier.testTag(adsTag)) },
                content = { Text("Content", modifier = Modifier.testTag(contentTag)) },
            )
        }

        composeTestRule.onNodeWithTag(adsTag).assertDoesNotExist()
        composeTestRule.onNodeWithTag(contentTag).assertExists()
    }
}
