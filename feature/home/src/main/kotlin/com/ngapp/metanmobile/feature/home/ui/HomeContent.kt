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

package com.ngapp.metanmobile.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource

@Composable
internal fun HomeContent(
    modifier: Modifier = Modifier,
    pinnedNewsList: List<UserNewsResource>,
    lastNewsList: List<UserNewsResource>,
    nearestStation: UserStationResource?,
    cngPrice: PriceResource?,
    pinnedFaqList: List<FaqResource>,
    career: CareerResource?,
    onNewsDetailClick: (String) -> Unit,
    onShowAllNewsClick: () -> Unit,
    onSeeAllFaqClick: () -> Unit,
    onSeeAllCareersClick: () -> Unit,
    onStationDetailClick: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item("contentHeader") {
            Surface(shadowElevation = 4.dp) {
                HomeHeaderView(
                    lastNewsItems = lastNewsList,
                    pinnedNews = pinnedNewsList,
                    onShowAllNewsClick = onShowAllNewsClick,
                    onNewsDetailClick = onNewsDetailClick
                )
            }
        }
        item("contentWidgetUserLocation") {
            Surface(shadowElevation = 4.dp) {
                HomeWidgetUserLocationView(
                    nearestStation = nearestStation,
                    cngPrice = cngPrice,
                    onStationDetailClick = onStationDetailClick,
                )
            }
        }
        item("contentCalculators") {
            Surface(shadowElevation = 4.dp) {
                HomeWidgetCalculatorsView()
            }
        }
        item("contentFaq") {
            Surface(shadowElevation = 4.dp) {
                HomeWidgetFaqView(
                    pinnedFaqItems = pinnedFaqList,
                    onSeeAllClick = onSeeAllFaqClick
                )
            }
        }
        if (career != null) {
            item("contentDisclaimer") {
                CareerWidget(
                    career = career,
                    onSeeAllClick = onSeeAllCareersClick
                )
            }
        }
    }
}