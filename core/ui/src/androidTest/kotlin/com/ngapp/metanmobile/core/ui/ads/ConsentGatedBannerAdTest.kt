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

package com.ngapp.metanmobile.core.ui.ads

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

private const val bannerAdTag = "bannerAd"

/**
 * [ConsentGatedBannerAd] must not render [MainBannerAd] - and so must not trigger a real ad
 * request - until [ConsentHelper] has resolved consent and allows ads. [MainBannerAd] is swapped
 * for a fake here so this stays an offline check of the gating, not a real ad load.
 */
class ConsentGatedBannerAdTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val fakeBannerAd: @Composable () -> Unit =
        { Text("Ad", modifier = Modifier.testTag(bannerAdTag)) }

    @Test
    fun bannerAd_isShown_whenConsentAllowsAds() {
        composeTestRule.setContent {
            ConsentGatedBannerAd(canShowAds = true, bannerAd = fakeBannerAd)
        }

        composeTestRule.onNodeWithTag(bannerAdTag).assertExists()
    }

    @Test
    fun bannerAd_isHidden_whenConsentDoesNotAllowAds() {
        composeTestRule.setContent {
            ConsentGatedBannerAd(canShowAds = false, bannerAd = fakeBannerAd)
        }

        composeTestRule.onNodeWithTag(bannerAdTag).assertDoesNotExist()
    }
}
