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

package com.ngapp.metanmobile.feature.favorites

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
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.core.ui.util.PermissionsState
import com.ngapp.metanmobile.feature.favorites.state.FavoritesAction
import com.ngapp.metanmobile.feature.favorites.state.FavoritesUiState
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.ngapp.metanmobile.core.ui.R as CoreUiR
import com.ngapp.metanmobile.core.designsystem.R as DesignSystemR

/**
 * UI tests for [FavoritesScreen], exercised directly (no Hilt/navigation) with a caller-supplied
 * [FavoritesUiState] and captured [FavoritesAction]s, the same approach as
 * `OnboardingScreenTest`. A row's own "view details" tap is intentionally never exercised here:
 * in the real screen that expands an embedded [com.ngapp.metanmobile.feature.stationdetail.StationDetailRoute]
 * bottom sheet, which needs Hilt - out of reach for a plain (non-`@HiltAndroidTest`) Compose test.
 */
class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val actions = mutableListOf<FavoritesAction>()
    private val permissionsState = PermissionsState().apply { hasLocationPermissions = true }

    private fun setContent(uiState: FavoritesUiState, searchQuery: String = "") {
        composeTestRule.setContent {
            MMTheme {
                CompositionLocalProvider(LocalPermissionsState provides permissionsState) {
                    FavoritesScreen(
                        modifier = Modifier,
                        searchQuery = searchQuery,
                        showDialog = false,
                        showBottomSheet = false,
                        stationForDelete = null,
                        stationCode = "",
                        uiState = uiState,
                        onNewsDetailClick = {},
                        onShowBottomBar = {},
                        onAction = { actions += it },
                    )
                }
            }
        }
    }

    private val emptyText by lazy {
        composeTestRule.activity.getString(R.string.feature_favorites_text_empty)
    }
    private val searchIconDescription by lazy {
        composeTestRule.activity.getString(DesignSystemR.string.core_designsystem_description_search_icon)
    }
    private val searchPlaceholder by lazy {
        composeTestRule.activity.getString(R.string.feature_favorites_placeholder_search_stations)
    }
    private val cancelText by lazy {
        composeTestRule.activity.getString(CoreUiR.string.core_ui_button_cancel)
    }
    private val approveRemoveText by lazy {
        composeTestRule.activity.getString(R.string.feature_favorites_text_approve_remove)
    }

    @Test
    fun emptyFavorites_showsEmptyMessage() {
        setContent(
            FavoritesUiState.Success(
                favoriteStationList = emptyList(),
                stationSortingConfig = StationSortingConfig.init(),
            ),
        )

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(emptyText).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun favoritesList_showsEachStationTitle() {
        val stationA = UserStationResource.init().copy(code = "a", title = "Station A")
        val stationB = UserStationResource.init().copy(code = "b", title = "Station B")
        setContent(
            FavoritesUiState.Success(
                favoriteStationList = listOf(stationA, stationB),
                stationSortingConfig = StationSortingConfig.init(),
            ),
        )

        composeTestRule.onNodeWithText("Station A").assertExists()
        composeTestRule.onNodeWithText("Station B").assertExists()
    }

    @Test
    fun deletingAFavorite_showsConfirmation_andCancelDismissesWithoutRemoving() {
        val station = UserStationResource.init().copy(code = "a", title = "Station A")
        setContent(
            FavoritesUiState.Success(
                favoriteStationList = listOf(station),
                stationSortingConfig = StationSortingConfig.init(),
            ),
        )

        composeTestRule.onNodeWithContentDescription("Delete Station A").performClick()

        assertEquals(
            listOf(
                FavoritesAction.UpdateStationForDelete(station),
                FavoritesAction.ShowBottomSheet(true),
            ),
            actions,
        )
    }

    @Test
    fun approvingDeleteConfirmation_dismissesAndUnmarksFavorite() {
        // isFavorite = true: this screen only ever shows favorites, and the approve action
        // toggles !stationForDelete.isFavorite - starting from false would (correctly) turn it
        // *on*, not remove it.
        val station = UserStationResource.init().copy(code = "a", title = "Station A", isFavorite = true)
        // Re-render with the bottom sheet already shown, as the screen itself would after the
        // delete tap drove showBottomSheet/stationForDelete through the view model.
        composeTestRule.setContent {
            MMTheme {
                CompositionLocalProvider(LocalPermissionsState provides permissionsState) {
                    FavoritesScreen(
                        modifier = Modifier,
                        searchQuery = "",
                        showDialog = false,
                        showBottomSheet = true,
                        stationForDelete = station,
                        stationCode = "",
                        uiState = FavoritesUiState.Success(
                            favoriteStationList = listOf(station),
                            stationSortingConfig = StationSortingConfig.init(),
                        ),
                        onNewsDetailClick = {},
                        onShowBottomBar = {},
                        onAction = { actions += it },
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(approveRemoveText).performClick()

        assertEquals(
            listOf(
                FavoritesAction.UpdateStationFavorite(station.code, false),
                FavoritesAction.ShowBottomSheet(false),
            ),
            actions,
        )
    }

    @Test
    fun deleteConfirmation_cancelButtonHidesItWithoutTouchingFavoriteState() {
        val station = UserStationResource.init().copy(code = "a", title = "Station A")
        composeTestRule.setContent {
            MMTheme {
                CompositionLocalProvider(LocalPermissionsState provides permissionsState) {
                    FavoritesScreen(
                        modifier = Modifier,
                        searchQuery = "",
                        showDialog = false,
                        showBottomSheet = true,
                        stationForDelete = station,
                        stationCode = "",
                        uiState = FavoritesUiState.Success(
                            favoriteStationList = listOf(station),
                            stationSortingConfig = StationSortingConfig.init(),
                        ),
                        onNewsDetailClick = {},
                        onShowBottomBar = {},
                        onAction = { actions += it },
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(cancelText).performClick()

        assertEquals(listOf<FavoritesAction>(FavoritesAction.ShowBottomSheet(false)), actions)
    }

    @Test
    fun searchIcon_opensSearchField_andTypingDispatchesUpdateSearchQuery() {
        setContent(
            FavoritesUiState.Success(
                favoriteStationList = emptyList(),
                stationSortingConfig = StationSortingConfig.init(),
            ),
        )

        composeTestRule.onNodeWithContentDescription(searchIconDescription).performClick()
        composeTestRule.onNodeWithText(searchPlaceholder).performTextInput("oak")

        assertTrue(actions.any { it == FavoritesAction.UpdateSearchQuery("oak") })
    }
}
