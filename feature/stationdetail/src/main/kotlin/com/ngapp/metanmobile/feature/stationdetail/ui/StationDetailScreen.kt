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

package com.ngapp.metanmobile.feature.stationdetail.ui

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.ngapp.metanmobile.core.designsystem.component.MMLinearWavyProgressIndicator
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailAction
import com.ngapp.metanmobile.feature.stationdetail.state.StationDetailUiState

@Composable
fun NewStationDetailScreen(
    modifier: Modifier,
    uiState: StationDetailUiState,
    onAction: (StationDetailAction) -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val isLoading = uiState == StationDetailUiState.Loading
    ReportDrawnWhen { isLoading }

    Column {
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
        }
    }
    TrackScreenViewEvent(screenName = "StationDetailScreen")
}

@PreviewScreenSizes
@Composable
private fun StationDetailScreenPreview() {
    MMTheme {
        NewStationDetailScreen(
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