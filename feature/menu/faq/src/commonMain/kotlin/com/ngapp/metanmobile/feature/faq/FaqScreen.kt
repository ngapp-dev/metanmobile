package com.ngapp.metanmobile.feature.faq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMLinearWavyProgressIndicator
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.animation.EmptyView
import com.ngapp.metanmobile.feature.faq.state.FaqUiState
import com.ngapp.metanmobile.feature.faq.ui.FaqContent
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FaqRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FaqViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncFailed by viewModel.syncFailed.collectAsStateWithLifecycle()

    FaqScreen(
        modifier = modifier,
        isSyncing = isSyncing,
        syncFailed = syncFailed,
        uiState = uiState,
        onBackClick = onBackClick,
        onRetryClick = viewModel::retrySync,
    )
}

@Composable
private fun FaqScreen(
    modifier: Modifier,
    isSyncing: Boolean,
    syncFailed: Boolean,
    uiState: FaqUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit = {},
) {
    val isLoading = uiState is FaqUiState.Loading

    FaqHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(
                visible = isSyncing || isLoading,
                enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
            ) {
                MMLinearWavyProgressIndicator()
            }
            when (uiState) {
                FaqUiState.Loading -> Unit
                is FaqUiState.Success -> {
                    if (uiState.faqList.isNotEmpty()) {
                        Surface(shadowElevation = 4.dp) {
                            FaqContent(faqList = uiState.faqList)
                        }
                    } else {
                        EmptyView(
                            modifier = modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            message = stringResource(SharedRes.strings.feature_menu_faq_text_empty),
                            canRetry = syncFailed,
                            onClickRetry = onRetryClick,
                        )
                    }
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
                titleRes = SharedRes.strings.feature_menu_faq_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}
