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

package com.ngapp.metanmobile.feature.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.feature.home.state.HomeUiState
import org.junit.Rule
import org.junit.Test
import com.ngapp.metanmobile.core.ui.R as CoreUiR

/**
 * UI tests for [HomeScreen]'s content-gating: while the very first sync hasn't finished yet,
 * nothing but the loading indicator (tested separately elsewhere) should be visible - not the
 * static, always-rendered widgets (the calculators tile, the user-location header), which used
 * to flash empty because Room's local flows turn `uiState` into `Success` as soon as they're
 * subscribed, well before the first network sync actually completes.
 *
 * The calculators tile's title is used as the marker for "content is showing" - it's the one
 * widget with no data dependency of its own, so it renders unconditionally whenever content is
 * allowed to render at all.
 */
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val calculatorsTitle by lazy {
        composeTestRule.activity.getString(CoreUiR.string.core_ui_title_calculate_profit)
    }
    private val emptyMessage by lazy {
        composeTestRule.activity.getString(R.string.feature_home_text_empty)
    }

    private fun setContent(
        uiState: HomeUiState,
        isSyncing: Boolean = false,
        syncFailed: Boolean = false,
    ) {
        composeTestRule.setContent {
            MMTheme {
                HomeScreen(
                    modifier = Modifier,
                    uiState = uiState,
                    reorderableList = HomeContentItem.entries,
                    isSyncing = isSyncing,
                    syncFailed = syncFailed,
                    isEditingUi = false,
                    isLastNewsExpended = true,
                    onNewsClick = {},
                    onNewsDetailClick = {},
                    onFaqListClick = {},
                    onCareersClick = {},
                    onCabinetClick = {},
                    onSettingsClick = {},
                    onShowBottomBar = {},
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun loadingState_showsNoContentWidgets() {
        setContent(uiState = HomeUiState.Loading, isSyncing = true)

        composeTestRule.onNodeWithText(calculatorsTitle).assertDoesNotExist()
    }

    @Test
    fun successStateButStillEmptyAndSyncing_showsNoContentWidgetsYet() {
        // The regression case: Room's local flows already emitted (uiState is Success), the
        // first sync hasn't reached the network yet (isSyncing), and there's nothing real to
        // show either way (everything empty).
        setContent(uiState = emptySuccess(), isSyncing = true, syncFailed = false)

        composeTestRule.onNodeWithText(calculatorsTitle).assertDoesNotExist()
    }

    @Test
    fun successStateWithRealData_showsContentWidgetsEvenWhileSyncing() {
        // A later background re-sync (isSyncing again) must not hide content that's already
        // loaded - only the initial empty/unknown moment should.
        setContent(uiState = emptySuccess().copy(cngPrice = PriceResource.init()), isSyncing = true)

        composeTestRule.onNodeWithText(calculatorsTitle).assertExists()
    }

    @Test
    fun successStateEmptyButSyncFinished_showsContentWidgets() {
        // Sync completed (isSyncing = false) without failing - genuinely nothing to show isn't
        // the same as "still loading", so the static widgets are allowed to render.
        setContent(uiState = emptySuccess(), isSyncing = false, syncFailed = false)

        composeTestRule.onNodeWithText(calculatorsTitle).assertExists()
    }

    @Test
    fun successStateEmptyAndSyncFailed_showsRetryMessage_notContentWidgets() {
        setContent(uiState = emptySuccess(), isSyncing = false, syncFailed = true)

        composeTestRule.onNodeWithText(calculatorsTitle).assertDoesNotExist()
        // The retry message sits behind an AnimatedVisibility gated on the Lottie animation's own
        // playback state, not on recomposition - wait for it explicitly rather than relying on
        // Compose Testing's regular idle sync, which doesn't track that clock.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(emptyMessage).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun emptySuccess() = HomeUiState.Success(
        pinnedNewsList = emptyList(),
        lastNewsList = emptyList(),
        cngPrice = null,
        nearestStation = null,
        pinnedFaqList = emptyList(),
        career = null,
    )
}
