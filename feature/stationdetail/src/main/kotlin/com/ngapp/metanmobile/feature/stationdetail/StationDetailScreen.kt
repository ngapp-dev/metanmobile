/*
 * Copyright 2025 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.feature.stationdetail

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailAction
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailUiState
import com.ngapp.metanmobile.feature.stationdetail.ui.StationDetailContent

@Composable
fun StationDetailRoute(
    stationCode: String? = "",
    onNewsDetailClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StationDetailViewModel = hiltViewModel(),
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
private fun StationDetailScreen(
    modifier: Modifier,
    uiState: StationDetailUiState,
    onAction: (StationDetailAction) -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val isLoading = uiState == StationDetailUiState.Loading
    ReportDrawnWhen { isLoading }

    when (uiState) {
        StationDetailUiState.Loading -> {
            val loadingContentDescription = "Station detail screen loading wheel"
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

        is StationDetailUiState.Success -> {
            Column(modifier) {
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
    TrackScreenViewEvent(screenName = "StationDetailScreen")
}

@PreviewScreenSizes
@Composable
private fun StationDetailScreenPreview() {
    MMTheme {
        StationDetailScreen(
            modifier = Modifier,
            uiState = StationDetailUiState.Success(
                stationDetail = UserStationResource.init(),
                cngPrice = PriceResource.init(),
                relatedNewsList = listOf(UserNewsResource.init())
            ),
            onAction = {},
            onNewsDetailClick = {},
            onBackClick = {}
        )
    }
}