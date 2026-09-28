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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.LocalMMTopBarPadding
import com.ngapp.metanmobile.core.designsystem.component.MMFilterSearchButtonsTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMFilterSearchFieldTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMLinearWavyProgressIndicator
import com.ngapp.metanmobile.core.designsystem.component.MMScaffold
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.DraggableScrollbar
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.scrollbarState
import com.ngapp.metanmobile.core.designsystem.component.withoutTop
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.TrackScrollJank
import com.ngapp.metanmobile.core.ui.alertdialogs.NewsSortingConfigDialog
import com.ngapp.metanmobile.core.ui.animation.EmptyView
import com.ngapp.metanmobile.feature.news.list.state.NewsAction
import com.ngapp.metanmobile.feature.news.list.state.NewsUiState
import com.ngapp.metanmobile.feature.news.list.ui.NewsContent
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/** The News list screen, shared by Android and iOS, built on the restored design system. */
@Composable
fun NewsRoute(
    onNewsDetailClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.search.collectAsStateWithLifecycle()
    val showDialog by viewModel.isSortingVisible.collectAsStateWithLifecycle()

    NewsScreen(
        modifier = modifier,
        uiState = uiState,
        searchQuery = searchQuery,
        showDialog = showDialog,
        onDetailClick = onNewsDetailClick,
        onAction = viewModel::dispatch,
    )
}

@Composable
internal fun NewsScreen(
    modifier: Modifier = Modifier,
    searchQuery: String,
    showDialog: Boolean,
    uiState: NewsUiState,
    onDetailClick: (String) -> Unit,
    onAction: (NewsAction) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val isLoading = uiState is NewsUiState.Loading

    val itemsAvailable = feedItemsSize(uiState)
    val gridState = rememberLazyGridState()
    val scrollbarState = gridState.scrollbarState(itemsAvailable = itemsAvailable)

    TrackScrollJank(scrollableState = gridState, stateName = "newsScreen:feed")

    NewsHeader(
        modifier = modifier,
        searchQuery = searchQuery,
        onAction = onAction,
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding.withoutTop())
        ) {
            // Box, not Column: the list starts under the glass top bar, so the indicator
            // overlays it (offset below the bar) instead of pushing it down.
            Box {
                AnimatedVisibility(
                    visible = isLoading,
                    enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
                ) {
                    MMLinearWavyProgressIndicator(Modifier.padding(top = LocalMMTopBarPadding.current))
                }
                when (uiState) {
                    NewsUiState.Loading -> Unit
                    is NewsUiState.Success -> {
                        if (showDialog) {
                            NewsSortingConfigDialog(
                                newsSortingConfig = uiState.sorting,
                                onConfirmClick = {
                                    onAction(NewsAction.UpdateSortingConfig(it))
                                    coroutineScope.launch { gridState.animateScrollToItem(0) }
                                },
                                onShowAlertDialog = { onAction(NewsAction.SetSortingVisible(it)) },
                            )
                        }
                        if (uiState.news.isNotEmpty() || uiState.pinnedNews.isNotEmpty()) {
                            Surface(shadowElevation = 4.dp) {
                                NewsContent(
                                    gridState = gridState,
                                    newsList = uiState.news,
                                    pinnedNewsList = uiState.pinnedNews,
                                    onDetailClick = onDetailClick,
                                )
                            }
                        } else {
                            EmptyView(
                                modifier = modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                message = stringResource(SharedRes.strings.news_empty),
                            )
                        }
                    }
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

private fun feedItemsSize(uiState: NewsUiState): Int = when (uiState) {
    NewsUiState.Loading -> 0
    is NewsUiState.Success -> if (uiState.news.isNotEmpty()) uiState.news.size + uiState.pinnedNews.size else 10
}

@Composable
private fun NewsHeader(
    modifier: Modifier,
    searchQuery: String,
    onAction: (NewsAction) -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    var showSearchMenu by rememberSaveable { mutableStateOf(false) }
    val title = if (searchQuery.isNotEmpty()) {
        stringResource(SharedRes.strings.news_toolbar_search_result, searchQuery)
    } else {
        stringResource(SharedRes.strings.news_toolbar_pinned)
    }

    MMScaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            if (!showSearchMenu) {
                MMFilterSearchButtonsTopAppBar(
                    title = title,
                    onSearchActionClick = { showSearchMenu = true },
                    onFilterActionClick = { onAction(NewsAction.SetSortingVisible(true)) },
                )
            } else {
                MMFilterSearchFieldTopAppBar(
                    searchText = searchQuery,
                    placeholderRes = SharedRes.strings.news_search_placeholder,
                    onSearchTextChanged = { onAction(NewsAction.UpdateSearchQuery(it)) },
                    onClearClick = { onAction(NewsAction.UpdateSearchQuery("")) },
                    onNavigationClick = {
                        onAction(NewsAction.UpdateSearchQuery(""))
                        showSearchMenu = false
                    },
                    onFilterActionClick = { onAction(NewsAction.SetSortingVisible(true)) },
                )
            }
        },
        content = pageContent,
    )
}
