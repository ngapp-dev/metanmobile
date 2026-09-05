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

package com.ngapp.metanmobile.feature.stations

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.location.LocationResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.core.ui.util.PermissionsState
import com.ngapp.metanmobile.feature.stations.state.StationsAction
import com.ngapp.metanmobile.feature.stations.state.StationsUiState
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue
import com.ngapp.metanmobile.core.designsystem.R as DesignSystemR
import com.ngapp.metanmobile.core.ui.R as CoreUiR

/**
 * UI tests for [StationsScreen], exercised directly (no Hilt/navigation) with a caller-supplied
 * [StationsUiState] and captured [StationsAction]s - the same approach as `OnboardingScreenTest`.
 *
 * A row's own "view details" tap is intentionally never exercised: like `FavoritesScreen`, it
 * expands an embedded [com.ngapp.metanmobile.feature.stationdetail.StationDetailRoute] bottom
 * sheet that needs Hilt. The MAP tab is also never switched to: `StationMapContent` renders a
 * real Google Map, which needs a Maps API key that this module's standalone test manifest doesn't
 * carry (only the app's does) - only its presence (the tab label) is checked.
 */
class StationsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val actions = mutableListOf<StationsAction>()
    private val permissionsState = PermissionsState().apply { hasLocationPermissions = true }

    private fun setContent(
        uiState: StationsUiState,
        isSyncing: Boolean = false,
        syncFailed: Boolean = false,
        searchQuery: String = "",
    ) {
        composeTestRule.setContent {
            MMTheme {
                CompositionLocalProvider(LocalPermissionsState provides permissionsState) {
                    StationsScreen(
                        modifier = Modifier,
                        isSyncing = isSyncing,
                        syncFailed = syncFailed,
                        searchQuery = searchQuery,
                        showDialog = false,
                        stationCode = "",
                        uiState = uiState,
                        onAction = { actions += it },
                        onNewsDetailClick = {},
                        onShowBottomBar = {},
                    )
                }
            }
        }
    }

    private fun successState(stations: List<UserStationResource> = emptyList()) = StationsUiState.Success(
        stationList = stations,
        userLocation = null,
        stationSortingConfig = StationSortingConfig.init(),
    )

    private val emptyText by lazy {
        composeTestRule.activity.getString(R.string.feature_stations_text_empty)
    }
    private val retryText by lazy {
        composeTestRule.activity.getString(CoreUiR.string.core_ui_button_retry)
    }
    private val searchIconDescription by lazy {
        composeTestRule.activity.getString(DesignSystemR.string.core_designsystem_description_search_icon)
    }
    private val searchPlaceholder by lazy {
        composeTestRule.activity.getString(R.string.feature_stations_placeholder_search_stations)
    }
    private val listTabTitle by lazy {
        composeTestRule.activity.getString(R.string.feature_stations_title_station_list)
    }
    private val mapTabTitle by lazy {
        composeTestRule.activity.getString(R.string.feature_stations_title_stations_map)
    }

    @Test
    fun emptyStations_showsEmptyMessage_andNoRetryWhenSyncHasNotFailed() {
        setContent(successState(), syncFailed = false)

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(emptyText).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(retryText).assertDoesNotExist()
    }

    @Test
    fun emptyStations_afterFailedSync_showsRetryThatDispatchesRetrySync() {
        setContent(successState(), syncFailed = true)

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(retryText).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(retryText).performClick()

        assertTrue(actions.contains(StationsAction.RetrySync))
    }

    @Test
    fun stationsList_showsEachStationTitle_andBothTabs() {
        val stationA = UserStationResource.init().copy(code = "a", title = "Station A")
        val stationB = UserStationResource.init().copy(code = "b", title = "Station B")
        setContent(successState(listOf(stationA, stationB)))

        composeTestRule.onNodeWithText("Station A").assertExists()
        composeTestRule.onNodeWithText("Station B").assertExists()
        composeTestRule.onNodeWithText(listTabTitle).assertExists()
        composeTestRule.onNodeWithText(mapTabTitle).assertExists()
    }

    @Test
    fun stationsList_reflectsKnownUserLocation() {
        val station = UserStationResource.init().copy(code = "a", title = "Station A", distanceBetween = 3.4)
        setContent(
            StationsUiState.Success(
                stationList = listOf(station),
                userLocation = LocationResource.init(),
                stationSortingConfig = StationSortingConfig.init(),
            ),
        )

        composeTestRule.onNodeWithText("Station A").assertExists()
    }

    @Test
    fun togglingFavorite_onNonFavoriteStation_dispatchesUpdateStationFavoriteTrue() {
        val station = UserStationResource.init().copy(code = "a", title = "Station A", isFavorite = false)
        setContent(successState(listOf(station)))
        val likeDescription = composeTestRule.activity.getString(
            CoreUiR.string.core_ui_description_like_icon,
            "Station A",
        )

        composeTestRule.onNodeWithContentDescription(likeDescription).performClick()

        assertTrue(actions.contains(StationsAction.UpdateStationFavorite("a", true)))
    }

    @Test
    fun togglingFavorite_onFavoriteStation_dispatchesUpdateStationFavoriteFalse() {
        val station = UserStationResource.init().copy(code = "a", title = "Station A", isFavorite = true)
        setContent(successState(listOf(station)))
        val dislikeDescription = composeTestRule.activity.getString(
            CoreUiR.string.core_ui_description_dislike_icon,
            "Station A",
        )

        composeTestRule.onNodeWithContentDescription(dislikeDescription).performClick()

        assertTrue(actions.contains(StationsAction.UpdateStationFavorite("a", false)))
    }

    @Test
    fun searchIcon_opensSearchField_andTypingDispatchesUpdateSearchQuery() {
        setContent(successState())

        composeTestRule.onNodeWithContentDescription(searchIconDescription).performClick()
        composeTestRule.onNodeWithText(searchPlaceholder).performTextInput("oak")

        assertTrue(actions.any { it == StationsAction.UpdateSearchQuery("oak") })
    }
}
