package com.ngapp.metanmobile.feature.cabinet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMCabinetTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMLinearWavyProgressIndicator
import com.ngapp.metanmobile.core.ui.animation.ErrorView
import com.ngapp.metanmobile.feature.cabinet.state.CabinetActions
import com.ngapp.metanmobile.feature.cabinet.state.CabinetUiState
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Shared WebView host for the personal cabinet, ported from master's Android `CabinetScreen`.
 * Uses `compose-webview-multiplatform` (its `captureBackPresses` default already routes the
 * system back gesture into the WebView's own history, mirroring master's `BackHandler`).
 */
@Composable
fun CabinetRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    url: String = "http://lk.metan.by/",
    viewModel: CabinetViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CabinetScreen(
        modifier = modifier,
        url = url,
        uiState = uiState,
        onBackClick = onBackClick,
        onAction = viewModel::triggerAction,
    )
}

@Composable
private fun CabinetScreen(
    modifier: Modifier,
    url: String,
    uiState: CabinetUiState,
    onBackClick: () -> Unit,
    onAction: (CabinetActions) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val webViewState = rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()
    val errorMessage = stringResource(SharedRes.strings.core_ui_error_loading_page) + "\n" +
        stringResource(SharedRes.strings.core_ui_error_page_not_avaliable)

    LaunchedEffect(webViewState.loadingState, webViewState.errorsForCurrentRequest.size) {
        val isLoading = webViewState.loadingState is LoadingState.Loading
        val hasMainFrameError = webViewState.errorsForCurrentRequest.any { it.isFromMainFrame }
        onAction(CabinetActions.UpdateUiState(uiState.copy(isLoading = isLoading, isError = hasMainFrameError)))
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MMCabinetTopAppBar(
                titleRes = SharedRes.strings.cabinet_title,
                onNavigationClick = onBackClick,
                onOpenInBrowserClicked = { uriHandler.openUri(url) },
                onGetAccessClicked = { uriHandler.openUri("https://metan.by/lk/?type=pda") },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                MMLinearWavyProgressIndicator()
            }
            if (uiState.isError) {
                ErrorView(
                    error = errorMessage,
                    action = { navigator.loadUrl(url) },
                )
            } else {
                WebView(
                    state = webViewState,
                    navigator = navigator,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
