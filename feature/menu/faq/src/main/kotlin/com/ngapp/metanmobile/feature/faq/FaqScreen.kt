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

package com.ngapp.metanmobile.feature.faq

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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.feature.faq.state.FaqUiState
import com.ngapp.metanmobile.feature.faq.ui.FaqContent

@Composable
internal fun FaqRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FaqViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    FaqScreen(
        modifier = modifier,
        isSyncing = isSyncing,
        uiState = uiState,
        onBackClick = onBackClick
    )
}

@Composable
private fun FaqScreen(
    modifier: Modifier,
    isSyncing: Boolean,
    uiState: FaqUiState,
    onBackClick: () -> Unit,
) {
    val isLoading = uiState is FaqUiState.Loading
    ReportDrawnWhen { !isSyncing && !isLoading }

    FaqHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                FaqUiState.Loading -> Unit
                is FaqUiState.Success -> {
                    Surface(shadowElevation = 4.dp) {
                        FaqContent(faqList = uiState.faqList)
                    }
                }
            }
            AnimatedVisibility(
                visible = isSyncing || isLoading,
                enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
            ) {
                val loadingContentDescription = "Faq screen loading wheel"
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
    TrackScreenViewEvent(screenName = "FaqScreen")
}

@Composable
private fun FaqHeader(
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
                titleResId = R.string.feature_menu_faq_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}


@PreviewScreenSizes
@Composable
private fun FaqScreenPreview() {
    MMTheme {
        FaqScreen(
            modifier = Modifier,
            isSyncing = false,
            uiState = FaqUiState.Success(faqList = listOf(FaqResource.init())),
            onBackClick = {},
        )
    }
}