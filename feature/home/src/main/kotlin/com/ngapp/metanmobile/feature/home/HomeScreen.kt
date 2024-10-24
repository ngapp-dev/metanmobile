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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMHomeTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.core.ui.util.PermissionsManager
import com.ngapp.metanmobile.feature.home.state.HomeAction
import com.ngapp.metanmobile.feature.home.state.HomeUiState
import com.ngapp.metanmobile.feature.home.ui.HomeContent

@Composable
internal fun HomeRoute(
    onNewsClick: () -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onStationDetailClick: (String) -> Unit,
    onFaqListClick: () -> Unit,
    onCareersClick: () -> Unit,
    onCabinetClick: () -> Unit,
    onSettingsClick: () -> Unit,
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
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        isSyncing = isSyncing,
        onNewsClick = onNewsClick,
        onNewsDetailClick = onNewsDetailClick,
        onStationDetailClick = onStationDetailClick,
        onFaqListClick = onFaqListClick,
        onCareersClick = onCareersClick,
        onCabinetClick = onCabinetClick,
        onSettingsClick = onSettingsClick,
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    isSyncing: Boolean,
    onNewsClick: () -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onStationDetailClick: (String) -> Unit,
    onFaqListClick: () -> Unit,
    onCareersClick: () -> Unit,
    onCabinetClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val isLoading = uiState is HomeUiState.Loading

    ReportDrawnWhen { !isSyncing && !isLoading }

    HomeHeader(
        modifier = modifier,
        onCabinetClick = onCabinetClick,
        onSettingsClick = onSettingsClick,
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                HomeUiState.Loading -> Unit
                is HomeUiState.Success -> {
                    HomeContent(
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
                        onStationDetailClick = onStationDetailClick,
                    )
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
    onCabinetClick: () -> Unit,
    onSettingsClick: () -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MMHomeTopAppBar(
                onUserClicked = onCabinetClick,
                onMenuClicked = onSettingsClick
            )
        },
        content = pageContent
    )
}