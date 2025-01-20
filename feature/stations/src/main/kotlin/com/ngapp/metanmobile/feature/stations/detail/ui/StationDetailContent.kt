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

package com.ngapp.metanmobile.feature.stations.detail.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.ItemDetailImageView
import com.ngapp.metanmobile.core.ui.news.NewsRow
import com.ngapp.metanmobile.core.ui.stations.createLocationIntent
import com.ngapp.metanmobile.feature.stations.detail.state.StationDetailAction
import com.ngapp.metanmobile.core.ui.R as CoreUiR

@Composable
internal fun StationDetailContent(
    modifier: Modifier = Modifier,
    stationDetail: UserStationResource,
    cngPrice: PriceResource?,
    relatedNewsList: List<UserNewsResource>,
    onAction: (StationDetailAction) -> Unit,
    onNewsDetailClick: (String) -> Unit,
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        item("detailImage") {
            ItemDetailImageView(imageUrl = stationDetail.detailPicture)
        }
        item("contentHeader") {
            StationDetailHeaderView(
                station = stationDetail,
                onToggleBookmark = {
                    onAction(
                        StationDetailAction.UpdateStationFavorite(
                            stationDetail.code, !stationDetail.isFavorite
                        )
                    )
                },
            )
        }
        item("contentBody") {
            StationDetailBodyView(
                station = stationDetail,
                cngPrice = cngPrice,
            )
        }
        if (relatedNewsList.isNotEmpty()) {
            item("relatedNews") {
                Text(
                    text = stringResource(CoreUiR.string.core_ui_title_related_news),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MMColors.cardBackgroundColor)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MMTypography.displayLarge,
                )
            }
            itemsIndexed(items = relatedNewsList, key = { i, news -> news.id }) { i, relatedNews ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MMColors.cardBackgroundColor)
                ) {
                    NewsRow(
                        news = relatedNews,
                        onDetailClick = { onNewsDetailClick(relatedNews.id) }
                    )
                    if (i < relatedNewsList.size - 1) {
                        MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
        item("contentFooter") {
            StationDetailFooterView(
                onNavigateButtonClick = { context.createLocationIntent(stationDetail) }
            )
        }
    }
}

