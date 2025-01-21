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

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMHomeTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.designsystem.theme.Green
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.stations.stationDetail.StationDetailBottomSheet
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.feature.home.state.HomeAction
import com.ngapp.metanmobile.feature.home.state.HomeUiState
import com.ngapp.metanmobile.feature.home.ui.HomeContent
import com.ngapp.metanmobile.feature.stationdetail.StationDetailRoute
import kotlinx.coroutines.launch
import com.ngapp.metanmobile.core.ui.R as CoreUiR

@Composable
internal fun HomeRoute(
    onNewsClick: () -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onFaqListClick: () -> Unit,
    onCareersClick: () -> Unit,
    onCabinetClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val permissionsState = LocalPermissionsState.current
    LaunchedEffect(permissionsState) {
        if (permissionsState.hasLocationPermissions) {
            viewModel.triggerAction(HomeAction.UpdateLocation(true))
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reorderableList by viewModel.reorderableList.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val isEditingUi by viewModel.isEditing.collectAsStateWithLifecycle()
    val isLastNewsExpended by viewModel.isLastNewsExpanded.collectAsStateWithLifecycle()

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        reorderableList = reorderableList,
        isSyncing = isSyncing,
        isEditingUi = isEditingUi,
        isLastNewsExpended = isLastNewsExpended,
        onNewsClick = onNewsClick,
        onNewsDetailClick = onNewsDetailClick,
        onFaqListClick = onFaqListClick,
        onCareersClick = onCareersClick,
        onCabinetClick = onCabinetClick,
        onSettingsClick = onSettingsClick,
        onShowBottomBar = onShowBottomBar,
        onAction = viewModel::triggerAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    reorderableList: List<HomeContentItem>,
    isSyncing: Boolean,
    isEditingUi: Boolean,
    isLastNewsExpended: Boolean,
    onNewsClick: () -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onFaqListClick: () -> Unit,
    onCareersClick: () -> Unit,
    onCabinetClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    onAction: (HomeAction) -> Unit,
) {
    val isLoading = uiState is HomeUiState.Loading
    ReportDrawnWhen { !isSyncing && !isLoading }
    var showTopAppBar by rememberSaveable { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            initialValue = SheetValue.Hidden,
        )
    )

    HomeHeader(
        modifier = modifier,
        isEditingUi = isEditingUi,
        showTopAppBar = showTopAppBar,
        onCabinetClick = onCabinetClick,
        onSettingsClick = onSettingsClick,
        onAction = onAction,
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                HomeUiState.Loading -> Unit
                is HomeUiState.Success -> {
                    StationDetailBottomSheet(
                        stationCode = uiState.nearestStation?.code,
                        bottomSheetState = bottomSheetScaffoldState,
                        openFullScreen = true,
                        onShowTopAppBar = { showTopAppBar = it },
                        onShowBottomBar = onShowBottomBar,
                        sheetContent = {
                            StationDetailRoute(
                                stationCode = uiState.nearestStation?.code,
                                onNewsDetailClick = onNewsDetailClick,
                                onBackClick = {
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.hide()
                                        showTopAppBar = true
                                        onShowBottomBar(true)
                                    }
                                },
                            )
                        }
                    ) {
                        HomeContent(
                            isEditingUi = isEditingUi,
                            isLastNewsExpended = isLastNewsExpended,
                            reorderableList = reorderableList,
                            pinnedNewsList = uiState.pinnedNewsList,
                            lastNewsList = uiState.lastNewsList,
                            nearestStation = uiState.nearestStation,
                            cngPrice = uiState.cngPrice,
                            pinnedFaqList = uiState.pinnedFaqList,
                            career = uiState.career,
                            onShowAllNewsClick = onNewsClick,
                            onSeeAllFaqClick = onFaqListClick,
                            onSeeAllCareersClick = onCareersClick,
                            onNewsDetailClick = onNewsDetailClick,
                            onStationDetailClick = {
                                showTopAppBar = false
                                onShowBottomBar(false)
                                coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
                            },
                            onAction = onAction,
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = isSyncing || isLoading,
                enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
            ) {
                val loadingContentDescription = "Home screen loading wheel"
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
    }
    TrackScreenViewEvent(screenName = "HomeScreen")
}

@Composable
private fun HomeHeader(
    modifier: Modifier,
    isEditingUi: Boolean,
    showTopAppBar: Boolean,
    onCabinetClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAction: (HomeAction) -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
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
                MMHomeTopAppBar(
                    onUserClicked = onCabinetClick,
                    onEditClicked = { onAction(HomeAction.EditUi(!isEditingUi)) },
                    onMenuClicked = onSettingsClick
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isEditingUi,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { onAction(HomeAction.SaveUi) },
                    containerColor = Green,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    shape = CircleShape,
                ) {
                    Text(
                        text = stringResource(CoreUiR.string.core_ui_button_save_changes),
                        style = MaterialTheme.typography.titleLarge,
                        color = White,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = pageContent
    )
}