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

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMFilterSearchButtonsTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMFilterSearchFieldTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.DraggableScrollbar
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.scrollbarState
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.TrackScrollJank
import com.ngapp.metanmobile.core.ui.alertdialogs.StationsSortAndFilterConfigDialog
import com.ngapp.metanmobile.core.ui.lottie.LottieEmptyView
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.feature.favorites.state.FavoritesAction
import com.ngapp.metanmobile.feature.favorites.state.FavoritesUiState
import com.ngapp.metanmobile.feature.favorites.ui.FavoritesBottomSheetContent
import com.ngapp.metanmobile.feature.favorites.ui.FavoritesContent
import com.ngapp.metanmobile.feature.stationdetail.ui.StationDetailBottomSheet
import kotlinx.coroutines.launch

@Composable
internal fun FavoritesRoute(
    onNewsDetailClick: (String) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val permissionsState = LocalPermissionsState.current
    LaunchedEffect(permissionsState) {
        if (permissionsState.hasLocationPermissions) {
            viewModel.triggerAction(FavoritesAction.UpdateLocation(true))
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val showDialog by viewModel.showDialog.collectAsStateWithLifecycle()
    val showBottomSheet by viewModel.showBottomSheet.collectAsStateWithLifecycle()
    val stationForDelete by viewModel.stationForDelete.collectAsStateWithLifecycle()
    val stationCode by viewModel.stationCode.collectAsStateWithLifecycle()

    FavoritesScreen(
        modifier = modifier,
        searchQuery = searchQuery,
        showDialog = showDialog,
        showBottomSheet = showBottomSheet,
        stationForDelete = stationForDelete,
        stationCode = stationCode,
        uiState = uiState,
        onNewsDetailClick = onNewsDetailClick,
        onShowBottomBar = onShowBottomBar,
        onAction = viewModel::triggerAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesScreen(
    modifier: Modifier,
    searchQuery: String,
    showDialog: Boolean,
    showBottomSheet: Boolean,
    stationForDelete: UserStationResource?,
    stationCode: String,
    uiState: FavoritesUiState,
    onNewsDetailClick: (String) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    onAction: (FavoritesAction) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val isLoading = uiState is FavoritesUiState.Loading
    ReportDrawnWhen { !isLoading }
    val stationsBottomSheetState = rememberModalBottomSheetState()
    val itemsAvailable = feedItemsSize(uiState)
    val gridState = rememberLazyGridState()
    val scrollbarState = gridState.scrollbarState(itemsAvailable = itemsAvailable)
    var showTopAppBar by rememberSaveable { mutableStateOf(true) }
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            initialValue = SheetValue.Hidden,
        )
    )
    TrackScrollJank(scrollableState = gridState, stateName = "favoritesScreen:feed")

    if (showBottomSheet && stationForDelete != null) {
        ModalBottomSheet(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            sheetState = stationsBottomSheetState,
            contentColor = MMColors.primary,
            shape = RectangleShape,
            dragHandle = null,
            onDismissRequest = { onAction(FavoritesAction.ShowBottomSheet(false)) },
            content = {
                FavoritesBottomSheetContent(
                    station = stationForDelete,
                    onCancel = { onAction(FavoritesAction.ShowBottomSheet(false)) },
                    onApprove = {
                        onAction(
                            FavoritesAction.UpdateStationFavorite(
                                stationForDelete.code, !stationForDelete.isFavorite
                            )
                        )
                        onAction(FavoritesAction.ShowBottomSheet(false))
                    }
                )
            }
        )
    }

    FavoritesHeader(
        modifier = modifier,
        searchQuery = searchQuery,
        showTopAppBar = showTopAppBar,
        onAction = onAction,
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                FavoritesUiState.Loading -> Unit
                is FavoritesUiState.Success -> {
                    if (showDialog) {
                        StationsSortAndFilterConfigDialog(
                            stationSortingConfig = uiState.stationSortingConfig,
                            onConfirmClick = {
                                onAction(FavoritesAction.UpdateSortingConfig(it))
                                coroutineScope.launch { gridState.animateScrollToItem(0) }
                            },
                            onShowAlertDialog = { onAction(FavoritesAction.ShowAlertDialog(it)) }
                        )
                    }

                    if (uiState.favoriteStationList.isNotEmpty()) {
                        Surface(shadowElevation = 4.dp) {
                            StationDetailBottomSheet(
                                stationCode = stationCode,
                                bottomSheetState = bottomSheetScaffoldState,
                                onShowTopAppBar = { showTopAppBar = it },
                                onShowBottomBar = onShowBottomBar,
                                onNewsDetailClick = onNewsDetailClick,
                            ) {
                                FavoritesContent(
                                    gridState = gridState,
                                    favoriteStationsList = uiState.favoriteStationList,
                                    onAction = onAction,
                                    onDetailClick = {
                                        onAction(FavoritesAction.UpdateStationCode(it))
                                        showTopAppBar = false
                                        onShowBottomBar(false)
                                        coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }

                                    },
                                )
                            }
                        }
                    } else {
                        LottieEmptyView(
                            modifier = modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            message = stringResource(R.string.feature_favorites_text_empty)
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = isLoading,
                enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
            ) {
                val loadingContentDescription = "Favorites screen loading wheel"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    MMOverlayLoadingWheel(
                        modifier = Modifier.align(Alignment.Center),
                        contentDesc = loadingContentDescription,
                    )
                }
            }
            gridState.DraggableScrollbar(
                modifier = Modifier
                    .fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(horizontal = 2.dp)
                    .align(Alignment.CenterEnd),
                state = scrollbarState,
                orientation = Orientation.Vertical,
                onThumbMoved = gridState.rememberDraggableScroller(itemsAvailable = itemsAvailable),
            )
        }
    }
    TrackScreenViewEvent(screenName = "FavoriteScreen")
}

private fun feedItemsSize(uiState: FavoritesUiState): Int {
    val feedSize = when (uiState) {
        FavoritesUiState.Loading -> 0
        is FavoritesUiState.Success -> uiState.favoriteStationList.size
    }
    return feedSize
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesHeader(
    modifier: Modifier,
    searchQuery: String,
    showTopAppBar: Boolean,
    onAction: (FavoritesAction) -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    var showSearchMenu by rememberSaveable { mutableStateOf(false) }
    val title = if (searchQuery.isNotEmpty()) {
        stringResource(id = R.string.feature_favorites_toolbar_search_result, searchQuery)
    } else {
        stringResource(id = R.string.feature_favorites_toolbar_title)
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            AnimatedVisibility(
                visible = showTopAppBar,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            ) {
                if (!showSearchMenu) {
                    MMFilterSearchButtonsTopAppBar(
                        title = title,
                        onSearchActionClick = { showSearchMenu = true },
                        onFilterActionClick = { onAction(FavoritesAction.ShowAlertDialog(true)) }
                    )
                } else {
                    MMFilterSearchFieldTopAppBar(
                        searchText = searchQuery,
                        placeholderRes = R.string.feature_favorites_placeholder_search_stations,
                        onSearchTextChanged = { onAction(FavoritesAction.UpdateSearchQuery(it)) },
                        onClearClick = { onAction(FavoritesAction.UpdateSearchQuery("")) },
                        onNavigationClick = {
                            onAction(FavoritesAction.UpdateSearchQuery(""))
                            showSearchMenu = false
                        },
                        onFilterActionClick = { onAction(FavoritesAction.ShowAlertDialog(true)) }
                    )
                }
            }
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Composable
private fun FavoritesScreenPreview() {
    MMTheme {
        FavoritesScreen(
            modifier = Modifier,
            searchQuery = "",
            showDialog = false,
            showBottomSheet = true,
            stationForDelete = null,
            stationCode = "",
            uiState = FavoritesUiState.Success(
                favoriteStationList = listOf(UserStationResource.init()),
                stationSortingConfig = StationSortingConfig.init(),
            ),
            onNewsDetailClick = {},
            onShowBottomBar = {},
            onAction = {},
        )
    }
}