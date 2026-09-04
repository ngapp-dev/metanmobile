/*
 * Copyright 2025 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.feature.stationdetail

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailAction
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailUiState
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.ngapp.metanmobile.core.designsystem.R as DesignSystemR

/**
 * UI tests for [StationDetailScreen], exercised directly (no Hilt/navigation) with a
 * caller-supplied [StationDetailUiState] and captured [StationDetailAction]s - the same approach
 * as `OnboardingScreenTest`. This is the content-only screen embedded by
 * [com.ngapp.metanmobile.feature.stationdetail.ui.StationDetailBottomSheet]; it has no Hilt
 * dependency of its own.
 */
class StationDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val actions = mutableListOf<StationDetailAction>()
    private var backClicked = false

    private fun setContent(uiState: StationDetailUiState) {
        composeTestRule.setContent {
            MMTheme {
                StationDetailScreen(
                    modifier = Modifier,
                    uiState = uiState,
                    onAction = { actions += it },
                    onNewsDetailClick = {},
                    onBackClick = { backClicked = true },
                )
            }
        }
    }

    private val shareDescription by lazy {
        composeTestRule.activity.getString(R.string.feature_stationdetail_description_share_station)
    }
    private val backDescription by lazy {
        composeTestRule.activity.getString(DesignSystemR.string.core_designsystem_description_back)
    }
    private val saveText by lazy {
        composeTestRule.activity.getString(R.string.feature_stationdetail_button_save)
    }
    private val savedText by lazy {
        composeTestRule.activity.getString(R.string.feature_stationdetail_button_saved)
    }

    @Test
    fun success_withNoStation_showsNoStationChrome() {
        setContent(
            StationDetailUiState.Success(stationDetail = null, cngPrice = null, relatedNewsList = emptyList()),
        )

        composeTestRule.onNodeWithContentDescription(shareDescription).assertDoesNotExist()
    }

    @Test
    fun success_showsTheStationTitle() {
        val station = UserStationResource.init().copy(title = "Grodno-2 station")
        setContent(
            StationDetailUiState.Success(
                stationDetail = station,
                cngPrice = null,
                relatedNewsList = emptyList(),
            ),
        )

        composeTestRule.onNodeWithText("Grodno-2 station").assertExists()
    }

    @Test
    fun closeButton_dispatchesOnBackClick() {
        val station = UserStationResource.init()
        setContent(
            StationDetailUiState.Success(stationDetail = station, cngPrice = null, relatedNewsList = emptyList()),
        )

        composeTestRule.onNodeWithContentDescription(backDescription).performClick()

        assertTrue(backClicked)
    }

    @Test
    fun shareButton_dispatchesShareStationWithTheDisplayedStation() {
        val station = UserStationResource.init()
        setContent(
            StationDetailUiState.Success(stationDetail = station, cngPrice = null, relatedNewsList = emptyList()),
        )

        composeTestRule.onNodeWithContentDescription(shareDescription).performClick()

        assertEquals(listOf<StationDetailAction>(StationDetailAction.ShareStation(station)), actions)
    }

    @Test
    fun saveButton_onNonFavorite_showsSave_andTogglesFavoriteOn() {
        val station = UserStationResource.init().copy(code = "st1", isFavorite = false)
        setContent(
            StationDetailUiState.Success(stationDetail = station, cngPrice = null, relatedNewsList = emptyList()),
        )
        composeTestRule.onNodeWithText(saveText).assertExists()

        composeTestRule.onNodeWithText(saveText).performClick()

        assertEquals(listOf<StationDetailAction>(StationDetailAction.UpdateStationFavorite("st1", true)), actions)
    }

    @Test
    fun saveButton_onFavorite_showsSaved_andTogglesFavoriteOff() {
        val station = UserStationResource.init().copy(code = "st1", isFavorite = true)
        setContent(
            StationDetailUiState.Success(stationDetail = station, cngPrice = null, relatedNewsList = emptyList()),
        )
        composeTestRule.onNodeWithText(savedText).assertExists()

        composeTestRule.onNodeWithText(savedText).performClick()

        assertEquals(listOf<StationDetailAction>(StationDetailAction.UpdateStationFavorite("st1", false)), actions)
    }
}
