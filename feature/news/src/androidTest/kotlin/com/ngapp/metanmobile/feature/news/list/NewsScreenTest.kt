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

package com.ngapp.metanmobile.feature.news.list

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.feature.news.R
import com.ngapp.metanmobile.feature.news.list.state.NewsAction
import com.ngapp.metanmobile.feature.news.list.state.NewsUiState
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.ngapp.metanmobile.core.designsystem.R as DesignSystemR

/**
 * UI tests for [NewsScreen], exercised directly (no Hilt/navigation) with a caller-supplied
 * [NewsUiState] and captured [NewsAction]s / detail clicks - the same approach as
 * `OnboardingScreenTest`. Unlike `FavoritesScreen`/`StationsScreen`, a row's detail click here is
 * just the plain `onDetailClick: (String) -> Unit` callback (no embedded Hilt bottom sheet), so
 * it's safe to exercise directly.
 */
class NewsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val actions = mutableListOf<NewsAction>()
    private var clickedNewsId: String? = null

    private fun setContent(uiState: NewsUiState, isSyncing: Boolean = false, syncFailed: Boolean = false) {
        composeTestRule.setContent {
            MMTheme {
                NewsScreen(
                    modifier = Modifier,
                    isSyncing = isSyncing,
                    syncFailed = syncFailed,
                    searchQuery = "",
                    showDialog = false,
                    uiState = uiState,
                    onDetailClick = { clickedNewsId = it },
                    onAction = { actions += it },
                )
            }
        }
    }

    private val emptyText by lazy {
        composeTestRule.activity.getString(R.string.feature_news_text_empty)
    }
    private val retryText by lazy {
        composeTestRule.activity.getString(com.ngapp.metanmobile.core.ui.R.string.core_ui_button_retry)
    }
    private val searchIconDescription by lazy {
        composeTestRule.activity.getString(DesignSystemR.string.core_designsystem_description_search_icon)
    }
    private val searchPlaceholder by lazy {
        composeTestRule.activity.getString(R.string.feature_news_placeholder_search_news)
    }

    private fun emptyUiState() = NewsUiState.Success(
        newsList = emptyList(),
        pinnedNewsList = emptyList(),
        newsSortingConfig = NewsSortingConfig.init(),
    )

    @Test
    fun emptyNews_showsEmptyMessage_andNoRetryWhenSyncHasNotFailed() {
        setContent(emptyUiState(), syncFailed = false)

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(emptyText).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(retryText).assertDoesNotExist()
    }

    @Test
    fun emptyNews_afterFailedSync_showsRetryThatDispatchesRetrySync() {
        setContent(emptyUiState(), syncFailed = true)

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(retryText).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(retryText).performClick()

        assertTrue(actions.contains(NewsAction.RetrySync))
    }

    @Test
    fun newsList_showsRegularAndPinnedTitles() {
        val pinned = UserNewsResource.init().copy(id = "1", isPinned = 1, title = "Pinned headline")
        val regular = UserNewsResource.init().copy(id = "2", isPinned = 0, title = "Regular headline")
        setContent(
            NewsUiState.Success(
                newsList = listOf(regular),
                pinnedNewsList = listOf(pinned),
                newsSortingConfig = NewsSortingConfig.init(),
            ),
        )

        composeTestRule.onNodeWithText("Regular headline").assertExists()
        composeTestRule.onNodeWithText("Pinned headline").assertExists()
    }

    @Test
    fun clickingARegularNewsRow_reportsItsId() {
        val regular = UserNewsResource.init().copy(id = "2", isPinned = 0, title = "Regular headline")
        setContent(
            NewsUiState.Success(
                newsList = listOf(regular),
                pinnedNewsList = emptyList(),
                newsSortingConfig = NewsSortingConfig.init(),
            ),
        )

        composeTestRule.onNodeWithText("Regular headline").performClick()

        assertEquals("2", clickedNewsId)
    }

    @Test
    fun searchIcon_opensSearchField_andTypingDispatchesUpdateSearchQuery() {
        setContent(emptyUiState())

        composeTestRule.onNodeWithContentDescription(searchIconDescription).performClick()
        composeTestRule.onNodeWithText(searchPlaceholder).performTextInput("oak")

        assertTrue(actions.any { it == NewsAction.UpdateSearchQuery("oak") })
    }
}
