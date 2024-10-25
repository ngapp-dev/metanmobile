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

package com.ngapp.metanmobile.feature.cabinet

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMCabinetTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.lottie.LottieErrorView
import com.ngapp.metanmobile.feature.cabinet.state.CabinetActions
import com.ngapp.metanmobile.feature.cabinet.state.CabinetUiState
import com.ngapp.metanmobile.core.ui.R as CorUiR

@Composable
internal fun CabinetRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    url: String = "http://lk.metan.by/",
    viewModel: CabinetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CabinetScreen(
        modifier = modifier,
        url = url,
        uiState = uiState,
        onBackClick = onBackClick,
        onAction = viewModel::triggerAction
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun CabinetScreen(
    modifier: Modifier,
    url: String,
    uiState: CabinetUiState,
    onBackClick: () -> Unit,
    onAction: (CabinetActions) -> Unit,
) {
    var backEnabled by remember { mutableStateOf(false) }
    var webView: WebView? = null
    val errorMessage =
        stringResource(id = CorUiR.string.core_ui_error_loading_page) + "\n" + stringResource(id = CorUiR.string.core_ui_error_page_not_avaliable)

    CabinetHeader(
        modifier = modifier,
        url = url,
        onBackClick = onBackClick,
    ) { padding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!uiState.isLoading) {
                AndroidView(
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
                                    onAction(CabinetActions.UpdateUiState(uiState.copy(isLoading = true, isError = false)))
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    onAction(CabinetActions.UpdateUiState(uiState.copy(isLoading = false)))
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    errorCode: Int,
                                    description: String?,
                                    failingUrl: String?,
                                ) {
                                    onAction(CabinetActions.UpdateUiState(uiState.copy(isLoading = false, isError = true)))
                                }
                            }
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true

                            loadUrl(url)
                            webView = this
                        }
                    }, update = { webView = it })
            } else if (uiState.isError) {
                LottieErrorView(
                    error = errorMessage,
                    paddingValues = padding,
                    action = { webView?.loadUrl(url) }
                )
            }
            AnimatedVisibility(
                visible = uiState.isLoading,
                enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
            ) {
                val loadingContentDescription = "Favorites screen loading wheel"
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
    BackHandler(enabled = backEnabled) { webView?.goBack() }
    TrackScreenViewEvent(screenName = "Cabinet")
}


@Composable
private fun CabinetHeader(
    modifier: Modifier,
    url: String,
    onBackClick: () -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MMCabinetTopAppBar(
                titleResId = R.string.feature_cabinet_toolbar_cabinet_title,
                onNavigationClick = onBackClick,
                onOpenInBrowserClicked = { uriHandler.openUri(url) },
                onGetAccessClicked = { uriHandler.openUri("https://metan.by/lk/?type=pda") }
            )
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Preview
@Composable
private fun CabinetScreenPreview() {
    MMTheme {
        CabinetScreen(
            modifier = Modifier,
            url = "",
            uiState = CabinetUiState(),
            onBackClick = {},
            onAction = {}
        )
    }
}