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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.component.reorderable.ReorderHapticFeedbackType
import com.ngapp.metanmobile.core.designsystem.component.reorderable.ReorderableItem
import com.ngapp.metanmobile.core.designsystem.component.reorderable.rememberReorderHapticFeedback
import com.ngapp.metanmobile.core.designsystem.component.reorderable.rememberReorderableLazyListState
import com.ngapp.metanmobile.core.designsystem.component.reorderable.reorderableItemModifier
import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.model.home.HomeContentItem.CALCULATORS
import com.ngapp.metanmobile.core.model.home.HomeContentItem.CAREER
import com.ngapp.metanmobile.core.model.home.HomeContentItem.FAQ
import com.ngapp.metanmobile.core.model.home.HomeContentItem.USER_LOCATION
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource

@Composable
internal fun HomeContent(
    isEditingUi: Boolean,
    reorderableList: List<HomeContentItem>,
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
    onReorderList: (List<HomeContentItem>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val haptic = rememberReorderHapticFeedback()
    val reorderableLazyColumnState = rememberReorderableLazyListState(listState) { from, to ->
        val temporaryList = reorderableList.toMutableList().apply {
            val fromIndex: Int = indexOfFirst { it == from.key }
            val toIndex: Int = indexOfFirst { it == to.key }
            add(toIndex, removeAt(fromIndex))
        }
        onReorderList(temporaryList)
        haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item("contentHeader") {
            HomeHeaderView(
                idEditing = isEditingUi,
                lastNewsItems = lastNewsList,
                pinnedNews = pinnedNewsList,
                onShowAllNewsClick = onShowAllNewsClick,
                onNewsDetailClick = onNewsDetailClick,
            )
        }
        items(reorderableList, key = { it }) { item ->
            ReorderableItem(reorderableLazyColumnState, item) {
                val interactionSource = remember { MutableInteractionSource() }
                val reorderableItemModifier = reorderableItemModifier(haptic, interactionSource)
                when (item) {
                    USER_LOCATION -> {
                        HomeWidgetUserLocationView(
                            isEditingUi = isEditingUi,
                            nearestStation = nearestStation,
                            cngPrice = cngPrice,
                            onStationDetailClick = onStationDetailClick,
                            reorderableItemModifier = reorderableItemModifier,
                        )
                    }

                    CALCULATORS -> {
                        HomeWidgetCalculatorsView(
                            isEditingUi = isEditingUi,
                            reorderableItemModifier = reorderableItemModifier,
                        )
                    }

                    FAQ -> {
                        HomeWidgetFaqView(
                            isEditingUi = isEditingUi,
                            pinnedFaqItems = pinnedFaqList,
                            onSeeAllClick = onSeeAllFaqClick,
                            reorderableItemModifier = reorderableItemModifier,
                        )
                    }

                    CAREER -> {
                        if (career != null) {
                            CareerWidget(
                                isEditingUi = isEditingUi,
                                career = career,
                                onSeeAllClick = onSeeAllCareersClick,
                                reorderableItemModifier = reorderableItemModifier,
                            )
                        }
                    }
                }
            }
        }
        if (isEditingUi) {
            item("safeDrawing") {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}
