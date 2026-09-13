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

package com.ngapp.metanmobile.feature.news.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMLinearWavyProgressIndicator
import com.ngapp.metanmobile.core.designsystem.component.MMNavShareButtonsTopAppBar
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailAction
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailUiState
import com.ngapp.metanmobile.feature.news.detail.ui.NewsDetailContent
import org.koin.compose.viewmodel.koinViewModel

/** Shared detail page for a news item, built on the restored design system. */
@Composable
fun NewsDetailRoute(
    newsId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(newsId) { viewModel.dispatch(NewsDetailAction.SetNewsId(newsId)) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NewsDetailScreen(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onAction = viewModel::dispatch,
    )
}

@Composable
internal fun NewsDetailScreen(
    modifier: Modifier = Modifier,
    uiState: NewsDetailUiState,
    onBackClick: () -> Unit,
    onAction: (NewsDetailAction) -> Unit,
) {
    // The top app bar must render regardless of uiState — otherwise, while Loading, nothing
    // consumes the top window insets and the content draws straight under the status bar.
    Column(modifier) {
        MMNavShareButtonsTopAppBar(
            onNavigationClick = onBackClick,
            onShareActionClick = { onAction(NewsDetailAction.ShareNews) },
        )
        when (uiState) {
            NewsDetailUiState.Loading -> {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
                ) {
                    MMLinearWavyProgressIndicator()
                }
            }

            is NewsDetailUiState.Success -> {
                NewsDetailContent(news = uiState.news)
            }
        }
    }
    TrackScreenViewEvent(screenName = "NewsDetailScreen")
}
