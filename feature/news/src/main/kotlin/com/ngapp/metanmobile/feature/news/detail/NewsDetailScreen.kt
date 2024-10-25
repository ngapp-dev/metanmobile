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

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMNavShareButtonsTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailAction
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailUiState
import com.ngapp.metanmobile.feature.news.detail.ui.NewsDetailContent

@Composable
internal fun NewsDetailRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NewsDetailScreen(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onAction = viewModel::triggerAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsDetailScreen(
    modifier: Modifier,
    uiState: NewsDetailUiState,
    onBackClick: () -> Unit,
    onAction: (NewsDetailAction) -> Unit,
) {
    ReportDrawnWhen { uiState is NewsDetailUiState.Loading }

    when (uiState) {
        is NewsDetailUiState.Success -> {
            Column(modifier) {
                MMNavShareButtonsTopAppBar(
                    onNavigationClick = onBackClick,
                    onShareActionClick = { onAction(NewsDetailAction.ShareNews(uiState.news)) }
                )
                NewsDetailContent(news = uiState.news)
            }
        }

        NewsDetailUiState.Loading -> {
            val loadingContentDescription = "News detail screen loading wheel"
            Box(
                modifier = modifier
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
    TrackScreenViewEvent(screenName = "NewsDetailScreen")
}

@PreviewScreenSizes
@Preview
@Composable
private fun NewsDetailScreenPreview() {
    MMTheme {
        NewsDetailScreen(
            modifier = Modifier,
            uiState = NewsDetailUiState.Success(news = NewsResource.init()),
            onBackClick = {},
            onAction = {}
        )
    }
}