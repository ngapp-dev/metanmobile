package com.ngapp.metanmobile.feature.careers

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
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.LocalMMTopBarPadding
import com.ngapp.metanmobile.core.designsystem.component.MMLinearWavyProgressIndicator
import com.ngapp.metanmobile.core.designsystem.component.MMScaffold
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.DraggableScrollbar
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.ngapp.metanmobile.core.designsystem.component.scrollbar.scrollbarState
import com.ngapp.metanmobile.core.designsystem.component.withoutTop
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.TrackScrollJank
import com.ngapp.metanmobile.core.ui.animation.EmptyView
import com.ngapp.metanmobile.feature.careers.state.CareersUiState
import com.ngapp.metanmobile.feature.careers.ui.CareersContent
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CareersRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CareersViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncFailed by viewModel.syncFailed.collectAsStateWithLifecycle()

    CareersScreen(
        modifier = modifier,
        isSyncing = isSyncing,
        syncFailed = syncFailed,
        uiState = uiState,
        onBackClick = onBackClick,
        onRetryClick = viewModel::retrySync,
    )
}

@Composable
private fun CareersScreen(
    modifier: Modifier,
    isSyncing: Boolean,
    syncFailed: Boolean,
    uiState: CareersUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit = {},
) {
    val isLoading = uiState is CareersUiState.Loading
    val itemsAvailable = feedItemsSize(uiState)
    val staggeredGridState = rememberLazyStaggeredGridState()
    val scrollbarState = staggeredGridState.scrollbarState(itemsAvailable = itemsAvailable)
    TrackScrollJank(scrollableState = staggeredGridState, stateName = "careersScreen:feed")

    CareersHeader(
        modifier = modifier,
        onBackClick = onBackClick
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
                    visible = isSyncing || isLoading,
                    enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
                ) {
                    MMLinearWavyProgressIndicator(Modifier.padding(top = LocalMMTopBarPadding.current))
                }
                when (uiState) {
                    CareersUiState.Loading -> Unit
                    is CareersUiState.Success -> {
                        if (uiState.careers.isNotEmpty()) {
                            CareersContent(
                                modifier = modifier,
                                staggeredGridState = staggeredGridState,
                                careers = uiState.careers,
                            )
                        } else {
                            EmptyView(
                                modifier = modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                message = stringResource(SharedRes.strings.feature_menu_careers_text_empty),
                                canRetry = syncFailed,
                                onClickRetry = onRetryClick,
                            )
                        }
                    }
                }
            }
            staggeredGridState.DraggableScrollbar(
                modifier = Modifier
                    .fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(horizontal = 2.dp)
                    .align(Alignment.CenterEnd),
                state = scrollbarState,
                orientation = Orientation.Vertical,
                onThumbMoved = staggeredGridState.rememberDraggableScroller(itemsAvailable = itemsAvailable),
            )
        }
    }
    TrackScreenViewEvent(screenName = "CareerScreen")
}

private fun feedItemsSize(uiState: CareersUiState): Int {
    val feedSize = when (uiState) {
        CareersUiState.Loading -> 0
        is CareersUiState.Success -> {
            if (uiState.careers.isNotEmpty()) uiState.careers.size else 10
        }
    }
    return feedSize
}

@Composable
private fun CareersHeader(
    modifier: Modifier,
    onBackClick: () -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    MMScaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MMToolbarWithNavIcon(
                titleRes = SharedRes.strings.feature_menu_careers_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}
