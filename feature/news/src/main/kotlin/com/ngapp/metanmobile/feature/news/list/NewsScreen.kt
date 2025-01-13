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

import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingType
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.TrackScrollJank
import com.ngapp.metanmobile.core.ui.alertdialogs.NewsSortingConfigDialog
import com.ngapp.metanmobile.core.ui.lottie.LottieEmptyView
import com.ngapp.metanmobile.feature.news.R
import com.ngapp.metanmobile.feature.news.list.state.NewsAction
import com.ngapp.metanmobile.feature.news.list.state.NewsUiState
import com.ngapp.metanmobile.feature.news.list.ui.NewsContent
import kotlinx.coroutines.launch

@Composable
internal fun NewsRoute(
    onNewsDetailClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val showDialog by viewModel.showDialog.collectAsStateWithLifecycle()

    Log.e("NewsRoute", "NewsRoute: $uiState")

    NewsScreen(
        modifier = modifier,
        isSyncing = isSyncing,
        uiState = uiState,
        searchQuery = searchQuery,
        showDialog = showDialog,
        onDetailClick = onNewsDetailClick,
        onAction = viewModel::triggerAction
    )
}

@Composable
private fun NewsScreen(
    modifier: Modifier,
    isSyncing: Boolean,
    searchQuery: String,
    showDialog: Boolean,
    uiState: NewsUiState,
    onDetailClick: (String) -> Unit,
    onAction: (NewsAction) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val isLoading = uiState is NewsUiState.Loading

    ReportDrawnWhen { !isSyncing && !isLoading }

    val itemsAvailable = feedItemsSize(uiState)
    val gridState = rememberLazyGridState()
    val scrollbarState = gridState.scrollbarState(itemsAvailable = itemsAvailable)

    TrackScrollJank(scrollableState = gridState, stateName = "newsScreen:feed")

    NewsHeader(
        modifier = modifier,
        searchQuery = searchQuery,
        onAction = onAction
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                NewsUiState.Loading -> Unit
                is NewsUiState.Success -> {
                    if (showDialog) {
                        NewsSortingConfigDialog(
                            newsSortingConfig = uiState.newsSortingConfig,
                            onConfirmClick = {
                                onAction(NewsAction.UpdateSortingConfig(it))
                                coroutineScope.launch { gridState.animateScrollToItem(0) }
                            },
                            onShowAlertDialog = { onAction(NewsAction.ShowAlertDialog(it)) }
                        )
                    }
                    if (uiState.newsList.isNotEmpty() || uiState.pinnedNewsList.isNotEmpty()) {
                        Surface(shadowElevation = 4.dp) {
                            NewsContent(
                                gridState = gridState,
                                newsList = uiState.newsList,
                                pinnedNewsList = uiState.pinnedNewsList,
                                onDetailClick = onDetailClick,
                            )
                        }
                    } else {
                        LottieEmptyView(
                            modifier = modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            message = stringResource(R.string.feature_news_text_empty)
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = isSyncing || isLoading,
                enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
            ) {
                val loadingContentDescription = "News screen loading wheel"
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
    TrackScreenViewEvent(screenName = "NewsScreen")
}

private fun feedItemsSize(uiState: NewsUiState): Int {
    val feedSize = when (uiState) {
        NewsUiState.Loading -> 0
        is NewsUiState.Success -> {
            if (uiState.newsList.isNotEmpty()) uiState.newsList.size + uiState.pinnedNewsList.size else 10
        }
    }
    return feedSize
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsHeader(
    modifier: Modifier,
    searchQuery: String,
    onAction: (NewsAction) -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    var showSearchMenu by rememberSaveable { mutableStateOf(false) }
    val title = if (searchQuery.isNotEmpty()) {
        stringResource(id = R.string.feature_news_toolbar_search_result, searchQuery)
    } else {
        stringResource(id = R.string.feature_news_toolbar_pinned_news_title)
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            if (!showSearchMenu) {
                MMFilterSearchButtonsTopAppBar(
                    title = title,
                    onSearchActionClick = { showSearchMenu = true },
                    onFilterActionClick = { onAction(NewsAction.ShowAlertDialog(true)) }
                )
            } else {
                MMFilterSearchFieldTopAppBar(
                    searchText = searchQuery,
                    placeholderRes = R.string.feature_news_placeholder_search_news,
                    onSearchTextChanged = { onAction(NewsAction.UpdateSearchQuery(it)) },
                    onClearClick = { onAction(NewsAction.UpdateSearchQuery("")) },
                    onNavigationClick = {
                        onAction(NewsAction.UpdateSearchQuery(""))
                        showSearchMenu = false
                    },
                    onFilterActionClick = { onAction(NewsAction.ShowAlertDialog(true)) }
                )

            }
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Preview
@Composable
private fun NewsScreenPreview() {
    MMTheme {
        NewsScreen(
            modifier = Modifier,
            isSyncing = false,
            uiState = NewsUiState.Success(
                pinnedNewsList = emptyList(),
                newsList = emptyList(),
                newsSortingConfig = NewsSortingConfig(
                    sortingType = NewsSortingType.DATE,
                    sortingOrder = SortingOrder.DESC,
                )
            ),
            searchQuery = "",
            showDialog = false,
            onDetailClick = {},
            onAction = {},
        )
    }
}