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

package com.ngapp.metanmobile.feature.termsandconditions

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.feature.termsandconditions.state.TermsAndConditionUiState

private const val urlEn = "https://metan.by/upload/metanmobile/termsandconditions.html"
private const val urlRu = "https://metan.by/upload/metanmobile/termsandconditions_ru.html"

@Composable
internal fun TermsAndConditionsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TermsAndConditionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TermsAndConditionsScreen(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun TermsAndConditionsScreen(
    modifier: Modifier,
    uiState: TermsAndConditionUiState,
    onBackClick: () -> Unit,
) {
    val isLoading = uiState is TermsAndConditionUiState.Loading
    ReportDrawnWhen { !isLoading }

    TermsAndConditionsHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                TermsAndConditionUiState.Loading -> Unit
                is TermsAndConditionUiState.Success -> {
                    val url = when (uiState.languageConfig) {
                        LanguageConfig.RU, LanguageConfig.BE -> urlRu
                        else -> urlEn
                    }
                    var backEnabled by remember { mutableStateOf(false) }
                    var webView: WebView? = null

                    AndroidView(
                        modifier = modifier.fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(
                                        view: WebView,
                                        url: String?,
                                        favicon: Bitmap?,
                                    ) {
                                        backEnabled = view.canGoBack()
                                    }
                                }
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.cacheMode = WebSettings.LOAD_NO_CACHE

                                loadUrl(url)
                                webView = this
                            }
                        }, update = { webView = it })

                    BackHandler(enabled = backEnabled) { webView?.goBack() }
                }
            }
            AnimatedVisibility(
                visible = isLoading,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                ) + fadeOut(),
            ) {
                val loadingContentDescription = "Terms and conditions screen loading wheel"
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
    TrackScreenViewEvent(screenName = "TermsAndConditionsScreen")
}

@Composable
private fun TermsAndConditionsHeader(
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
                titleResId = R.string.feature_menu_legalregulations_termsandconditions_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Composable
private fun TermsAndConditionsPreview() {
    MMTheme {
        TermsAndConditionsScreen(
            modifier = Modifier,
            uiState = TermsAndConditionUiState.Success(languageConfig = LanguageConfig.RU),
            onBackClick = {},
        )
    }
}