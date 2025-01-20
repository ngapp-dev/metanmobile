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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.ngapp.metanmobile.feature.stations.list

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMFilterSearchButtonsTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMFilterSearchFieldTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.designsystem.component.MMTab
import com.ngapp.metanmobile.core.designsystem.component.MMTabRow
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.DraggableScrollbar
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.scrollbarState
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.TrackScrollJank
import com.ngapp.metanmobile.core.ui.alertdialogs.StationsSortAndFilterConfigDialog
import com.ngapp.metanmobile.core.ui.lottie.LottieEmptyView
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.feature.stations.R
import com.ngapp.metanmobile.feature.stations.list.StationTabs.LIST
import com.ngapp.metanmobile.feature.stations.list.StationTabs.MAP
import com.ngapp.metanmobile.feature.stations.list.state.StationsAction
import com.ngapp.metanmobile.feature.stations.list.state.StationsUiState
import com.ngapp.metanmobile.feature.stations.list.ui.StationListContent
import com.ngapp.metanmobile.feature.stations.list.ui.StationMapBottomSheet
import com.ngapp.metanmobile.feature.stations.list.ui.StationMapContent
import kotlinx.coroutines.launch

@Composable
internal fun StationsRoute(
    bottomSheetState: BottomSheetScaffoldState,
    onStationDetailClick: (String) -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StationsViewModel = hiltViewModel(),
) {
    val permissionsState = LocalPermissionsState.current
    LaunchedEffect(permissionsState) {
        if (permissionsState.hasLocationPermissions) {
            viewModel.triggerAction(StationsAction.UpdateLocation(true))
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val showDialog by viewModel.showDialog.collectAsStateWithLifecycle()
    val bottomSheetExpanded = bottomSheetState.bottomSheetState.currentValue != SheetValue.Expanded

    StationsScreen(
        modifier = modifier,
        isSyncing = isSyncing,
        searchQuery = searchQuery,
        showDialog = showDialog,
        showTopBar = bottomSheetExpanded,
        uiState = uiState,
        bottomSheetState = bottomSheetState,
        onAction = viewModel::triggerAction,
        onStationDetailClick = onStationDetailClick,
        onNewsDetailClick = onNewsDetailClick,
        onBackClick = onBackClick,
    )
}

@Composable
private fun StationsScreen(
    modifier: Modifier,
    isSyncing: Boolean,
    searchQuery: String,
    showDialog: Boolean,
    showTopBar: Boolean,
    uiState: StationsUiState,
    bottomSheetState: BottomSheetScaffoldState,
    onAction: (StationsAction) -> Unit,
    onStationDetailClick: (String) -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    StationsHeader(
        modifier = modifier,
        searchQuery = searchQuery,
        showTopBar = showTopBar,
        onAction = onAction,
    ) { padding ->

        val coroutineScope = rememberCoroutineScope()
        val isLoading = uiState is StationsUiState.Loading
        ReportDrawnWhen { !isSyncing && !isLoading }
        val itemsAvailable = feedItemsSize(uiState)
        val gridState = rememberLazyGridState()
        val scrollbarState = gridState.scrollbarState(itemsAvailable = itemsAvailable)
        TrackScrollJank(scrollableState = gridState, stateName = "stationsScreen:feed")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                StationsUiState.Loading -> Unit
                is StationsUiState.Success -> {
                    if (uiState.stationList.isNotEmpty()) {
                        Column {
                            val tabsName =
                                rememberSaveable { StationTabs.entries.map { it.titleResId } }
                            var selectedIndex by rememberSaveable { mutableIntStateOf(LIST.ordinal) }
                            AnimatedVisibility(
                                visible = showTopBar,
                                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                            ) {
                                MMTabRow(
                                    selectedTabIndex = selectedIndex,
                                    indicatorModifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .offset(y = (-5).dp)
                                ) {
                                    tabsName.forEachIndexed { index, stringResourceId ->
                                        MMTab(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = MMColors.cardBackgroundColor),
                                            selected = index == selectedIndex,
                                            onClick = { selectedIndex = index },
                                            text = {
                                                Text(
                                                    text = stringResource(id = stringResourceId),
                                                    style = MMTypography.headlineMedium,
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                            Crossfade(
                                targetState = selectedIndex,
                                label = "stationsScreen",
                            ) { page ->
                                if (showDialog) {
                                    StationsSortAndFilterConfigDialog(
                                        stationSortingConfig = uiState.stationSortingConfig,
                                        onConfirmClick = {
                                            onAction(StationsAction.UpdateSortingConfig(it))
                                            coroutineScope.launch { gridState.animateScrollToItem(0) }
                                        },
                                        onShowAlertDialog = {
                                            onAction(StationsAction.ShowAlertDialog(it))
                                        }
                                    )
                                }

                                when (StationTabs.entries[page]) {
                                    LIST -> {
                                        var stationCode by rememberSaveable { mutableStateOf("") }
                                        StationMapBottomSheet(
                                            stationCode = stationCode,
                                            bottomSheetState = bottomSheetState,
                                            onNewsDetailClick = onNewsDetailClick,
                                            onBackClick = onBackClick,
                                        ) {
                                            Box(modifier = modifier) {
                                                StationListContent(
                                                    modifier = modifier,
                                                    gridState = gridState,
                                                    stationsList = uiState.stationList,
                                                    onAction = onAction,
                                                    onDetailClick = {
                                                        stationCode = it
                                                        coroutineScope.launch { bottomSheetState.bottomSheetState.expand() }
                                                    },
                                                )
                                                gridState.DraggableScrollbar(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .windowInsetsPadding(WindowInsets.systemBars)
                                                        .padding(horizontal = 2.dp)
                                                        .align(Alignment.CenterEnd),
                                                    state = scrollbarState,
                                                    orientation = Orientation.Vertical,
                                                    onThumbMoved = gridState.rememberDraggableScroller(
                                                        itemsAvailable = itemsAvailable
                                                    ),
                                                )
                                            }
                                        }
                                        TrackScreenViewEvent(screenName = "StationListContent")
                                    }

                                    MAP -> {
                                        var stationCode by rememberSaveable { mutableStateOf("") }
                                        StationMapBottomSheet(
                                            stationCode = stationCode,
                                            bottomSheetState = bottomSheetState,
                                            onNewsDetailClick = onNewsDetailClick,
                                            onBackClick = onBackClick,
                                        ) {
                                            StationMapContent(
                                                modifier = modifier,
                                                stationList = uiState.stationList,
                                                userLocation = uiState.userLocation,
                                                onDetailClick = {
                                                    stationCode = it
                                                    coroutineScope.launch { bottomSheetState.bottomSheetState.partialExpand() }
                                                },
                                            )
                                        }
                                        TrackScreenViewEvent(screenName = "StationMapContent")
                                    }
                                }
                            }
                        }
                    } else {
                        LottieEmptyView(
                            modifier = modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            message = stringResource(R.string.feature_stations_text_empty)
                        )
                    }
                }
            }
            LoadingState(isSyncing = isSyncing, isLoading = isLoading)
        }
    }
}

private enum class StationTabs(val titleResId: Int) {
    LIST(R.string.feature_stations_title_station_list),
    MAP(R.string.feature_stations_title_stations_map)
}

@Composable
private fun LoadingState(
    isSyncing: Boolean,
    isLoading: Boolean,
) {
    AnimatedVisibility(
        visible = isSyncing || isLoading,
        enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
    ) {
        val loadingContentDescription = "Stations list loading wheel"
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
}

private fun feedItemsSize(uiState: StationsUiState): Int {
    val feedSize = when (uiState) {
        StationsUiState.Loading -> 0
        is StationsUiState.Success -> {
            if (uiState.stationList.isNotEmpty()) uiState.stationList.size else 10
        }
    }
    return feedSize
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationsHeader(
    modifier: Modifier,
    searchQuery: String,
    showTopBar: Boolean,
    onAction: (StationsAction) -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    var showSearchMenu by rememberSaveable { mutableStateOf(false) }
    val title = if (searchQuery.isNotEmpty()) {
        stringResource(id = R.string.feature_stations_toolbar_search_result, searchQuery)
    } else {
        stringResource(id = R.string.feature_stations_toolbar_title)
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            AnimatedVisibility(
                visible = showTopBar,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            ) {
                if (!showSearchMenu) {
                    MMFilterSearchButtonsTopAppBar(
                        title = title,
                        onSearchActionClick = { showSearchMenu = true },
                        onFilterActionClick = { onAction(StationsAction.ShowAlertDialog(true)) }
                    )
                } else {
                    MMFilterSearchFieldTopAppBar(
                        searchText = searchQuery,
                        placeholderRes = R.string.feature_stations_placeholder_search_stations,
                        onSearchTextChanged = { onAction(StationsAction.UpdateSearchQuery(it)) },
                        onClearClick = { onAction(StationsAction.UpdateSearchQuery("")) },
                        onNavigationClick = {
                            onAction(StationsAction.UpdateSearchQuery(""))
                            showSearchMenu = false
                        },
                        onFilterActionClick = { onAction(StationsAction.ShowAlertDialog(true)) }
                    )
                }
            }
        },
        content = pageContent
    )
}