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

package com.ngapp.metanmobile.feature.news.detail

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailAction
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailUiState
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.ngapp.metanmobile.core.designsystem.R as DesignSystemR

/**
 * UI tests for [NewsDetailScreen], exercised directly (no Hilt/navigation/SavedStateHandle) with
 * a caller-supplied [NewsDetailUiState] and captured [NewsDetailAction]s - the same approach as
 * `OnboardingScreenTest`.
 */
class NewsDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val actions = mutableListOf<NewsDetailAction>()
    private var backClicked = false

    private fun setContent(uiState: NewsDetailUiState) {
        composeTestRule.setContent {
            MMTheme {
                NewsDetailScreen(
                    modifier = Modifier,
                    uiState = uiState,
                    onBackClick = { backClicked = true },
                    onAction = { actions += it },
                )
            }
        }
    }

    private val backDescription by lazy {
        composeTestRule.activity.getString(DesignSystemR.string.core_designsystem_description_nav_icon)
    }
    private val shareDescription by lazy {
        composeTestRule.activity.getString(DesignSystemR.string.core_designsystem_description_share_icon)
    }

    @Test
    fun success_showsTheNewsTitle() {
        val news = NewsResource.init().copy(id = "1", title = "Requested article")
        setContent(NewsDetailUiState.Success(news = news))

        composeTestRule.onNodeWithText("Requested article").assertExists()
    }

    @Test
    fun backButton_worksRegardlessOfUiState() {
        setContent(NewsDetailUiState.Loading)

        composeTestRule.onNodeWithContentDescription(backDescription).performClick()

        assertTrue(backClicked)
    }

    @Test
    fun shareButton_onSuccess_dispatchesShareNewsWithTheDisplayedArticle() {
        val news = NewsResource.init().copy(id = "1", title = "Requested article")
        setContent(NewsDetailUiState.Success(news = news))

        composeTestRule.onNodeWithContentDescription(shareDescription).performClick()

        assertEquals(listOf<NewsDetailAction>(NewsDetailAction.ShareNews(news)), actions)
    }

    @Test
    fun shareButton_whileLoading_dispatchesNothing() {
        setContent(NewsDetailUiState.Loading)

        composeTestRule.onNodeWithContentDescription(shareDescription).performClick()

        assertTrue(actions.isEmpty())
    }
}
