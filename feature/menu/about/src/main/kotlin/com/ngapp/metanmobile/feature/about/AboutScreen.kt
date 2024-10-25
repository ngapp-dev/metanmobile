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

package com.ngapp.metanmobile.feature.about

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMAsyncImage
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.model.githubuser.GithubUserResource
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.feature.about.state.AboutUiState

@Composable
internal fun AboutRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AboutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    AboutScreen(
        modifier = modifier,
        isSyncing = isSyncing,
        uiState = uiState,
        onBackClick = onBackClick
    )
}

@Composable
private fun AboutScreen(
    modifier: Modifier,
    isSyncing: Boolean,
    uiState: AboutUiState,
    onBackClick: () -> Unit,
) {
    val isLoading = uiState is AboutUiState.Loading
    ReportDrawnWhen { !isSyncing && !isLoading }

    val uriHandler = LocalUriHandler.current

    AboutHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is AboutUiState.Loading -> Unit
                is AboutUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MMAsyncImage(
                            imageUrl = uiState.githubUser.avatarUrl
                        )
                        Spacer(Modifier.height(18.dp))
                        Text(
                            text = stringResource(R.string.feature_menu_about_text_developed_by),
                            style = MMTypography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = uiState.githubUser.name.orEmpty(),
                            style = MMTypography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.feature_menu_about_text_android_developer),
                            style = MMTypography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = uiState.githubUser.htmlUrl,
                            style = MMTypography.titleLarge,
                            modifier = Modifier
                                .clickable { uriHandler.openUri(uiState.githubUser.htmlUrl) }
                        )
                        Spacer(Modifier.height(18.dp))
                        Text(
                            text = stringResource(R.string.feature_menu_about_text_copyright_explanation),
                            style = MMTypography.headlineMedium,
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = isSyncing || isLoading,
                enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
            ) {
                val loadingContentDescription = "About screen loading wheel"
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
    TrackScreenViewEvent(screenName = "AboutScreen")
}

@Composable
private fun AboutHeader(
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
                titleResId = R.string.feature_menu_about_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Preview
@Composable
private fun AboutScreenPreview() {
    MMTheme {
        AboutScreen(
            modifier = Modifier,
            isSyncing = false,
            uiState = AboutUiState.Success(githubUser = GithubUserResource.init()),
            onBackClick = {},
        )
    }
}