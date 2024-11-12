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

package com.ngapp.metanmobile.feature.careers

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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.DraggableScrollbar
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.scrollbarState
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.TrackScrollJank
import com.ngapp.metanmobile.core.ui.lottie.LottieEmptyView
import com.ngapp.metanmobile.feature.careers.state.CareersUiState
import com.ngapp.metanmobile.feature.careers.ui.CareersContent

@Composable
internal fun CareersRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CareersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    CareersScreen(
        modifier = modifier,
        isSyncing = isSyncing,
        uiState = uiState,
        onBackClick = onBackClick,
    )
}

@Composable
private fun CareersScreen(
    modifier: Modifier,
    isSyncing: Boolean,
    uiState: CareersUiState,
    onBackClick: () -> Unit,
) {
    val isLoading = uiState is CareersUiState.Loading
    ReportDrawnWhen { !isLoading }
    val itemsAvailable = feedItemsSize(uiState)
    val staggeredGridState = rememberLazyStaggeredGridState()
    val scrollbarState = staggeredGridState.scrollbarState(itemsAvailable = itemsAvailable)
    TrackScrollJank(scrollableState = staggeredGridState, stateName = "careersScreen:feed")

    CareersHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                CareersUiState.Loading -> Unit
                is CareersUiState.Success -> {
                    if (uiState.careers.isNotEmpty()) {
                        CareersContent(
                            modifier = modifier,
                            staggeredGridState = staggeredGridState,
                            careers = uiState.careers,
                        )
                    } else {
                        LottieEmptyView(
                            modifier = modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            message = stringResource(R.string.feature_menu_careers_text_empty)
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = isSyncing || isLoading,
                enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
            ) {
                val loadingContentDescription = "Career screen loading wheel"
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
            staggeredGridState.DraggableScrollbar(
                modifier = Modifier
                    .fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(horizontal = 2.dp)
                    .align(Alignment.CenterEnd),
                state = scrollbarState,
                orientation = Orientation.Vertical,
                onThumbMoved = staggeredGridState.rememberDraggableScroller(itemsAvailable = itemsAvailable),
            )
        }
    }
    TrackScreenViewEvent(screenName = "CareerScreen")
}

private fun feedItemsSize(uiState: CareersUiState): Int {
    val feedSize = when (uiState) {
        CareersUiState.Loading -> 0
        is CareersUiState.Success -> {
            if (uiState.careers.isNotEmpty()) uiState.careers.size else 10
        }
    }
    return feedSize
}

@Composable
private fun CareersHeader(
    modifier: Modifier,
    onBackClick: () -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MMToolbarWithNavIcon(
                titleResId = R.string.feature_menu_careers_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Composable
private fun CareersScreenPreview() {
    MMTheme {
        CareersScreen(
            modifier = Modifier,
            isSyncing = false,
            uiState = CareersUiState.Success(
                careers = listOf(CareerResource.init())
            ),
            onBackClick = {},
        )
    }
}