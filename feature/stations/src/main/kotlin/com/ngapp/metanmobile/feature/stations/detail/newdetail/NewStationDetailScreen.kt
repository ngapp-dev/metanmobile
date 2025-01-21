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

package com.ngapp.metanmobile.feature.stations.detail.newdetail

import android.util.Log
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.ngapp.metanmobile.feature.stations.detail.newdetail.state.NewStationDetailAction
import com.ngapp.metanmobile.feature.stations.detail.newdetail.state.NewStationDetailUiState
import com.ngapp.metanmobile.feature.stations.detail.newdetail.ui.NewStationDetailContent

@Composable
internal fun NewStationDetailRoute(
    stationCode: String = "",
    onNewsDetailClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewStationDetailViewModel = hiltViewModel(),
) {
    var counter by rememberSaveable { mutableIntStateOf(0) }

    DisposableEffect(stationCode) {
        viewModel.triggerAction(NewStationDetailAction.LoadStationDetail(stationCode))

        onDispose {
            viewModel.triggerAction(NewStationDetailAction.ClearStationDetail)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState) {
        counter++
    }
    Log.e("NewStationDetailRoute", "stationCode: $stationCode")
    Log.e("NewStationDetailRoute", "uiState: ${uiState.stationDetail?.code}")
    Log.e("NewStationDetailRoute", "LaunchedEffect: $counter")
    NewStationDetailScreen(
        modifier = modifier,
        uiState = uiState,
        onAction = viewModel::triggerAction,
        onNewsDetailClick = onNewsDetailClick,
        onBackClick = onBackClick,
    )
}

@Composable
private fun NewStationDetailScreen(
    modifier: Modifier,
    uiState: NewStationDetailUiState,
    onAction: (NewStationDetailAction) -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val isLoading = uiState.isLoading
    ReportDrawnWhen { isLoading }

    when {
        isLoading -> {
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

        uiState.stationDetail != null -> {
            Column(modifier) {
                NewStationDetailContent(
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
        NewStationDetailScreen(
            modifier = Modifier,
            uiState = NewStationDetailUiState(
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