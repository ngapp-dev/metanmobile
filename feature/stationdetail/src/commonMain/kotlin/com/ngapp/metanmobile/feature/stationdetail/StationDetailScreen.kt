package com.ngapp.metanmobile.feature.stationdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMLinearWavyProgressIndicator
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailAction
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailUiState
import com.ngapp.metanmobile.feature.stationdetail.ui.StationDetailContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StationDetailRoute(
    modifier: Modifier = Modifier,
    stationCode: String? = "",
    onNewsDetailClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: StationDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(stationCode) {
        viewModel.triggerAction(StationDetailAction.SetStationCode(stationCode))
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StationDetailScreen(
        modifier = modifier,
        uiState = uiState,
        onAction = viewModel::triggerAction,
        onNewsDetailClick = onNewsDetailClick,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun StationDetailScreen(
    modifier: Modifier,
    uiState: StationDetailUiState,
    onAction: (StationDetailAction) -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val isLoading = uiState == StationDetailUiState.Loading

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        AnimatedVisibility(
            visible = isLoading,
            enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
        ) {
            MMLinearWavyProgressIndicator()
        }
        when (uiState) {
            StationDetailUiState.Loading -> Unit
            is StationDetailUiState.Success -> {
                if (uiState.stationDetail != null) {
                    StationDetailContent(
                        stationDetail = uiState.stationDetail,
                        cngPrice = uiState.cngPrice,
                        relatedNewsList = uiState.relatedNewsList,
                        onAction = onAction,
                        onNewsDetailClick = onNewsDetailClick,
                        onBackClick = onBackClick,
                    )
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "StationDetailScreen")
}
