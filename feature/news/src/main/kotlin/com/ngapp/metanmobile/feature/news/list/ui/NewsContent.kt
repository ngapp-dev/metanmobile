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

package com.ngapp.metanmobile.feature.news.list.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.ui.logNewsResourceOpened
import com.ngapp.metanmobile.core.ui.news.NewsRow
import com.ngapp.metanmobile.core.ui.news.PinnedNewsScreen

@Composable
internal fun NewsContent(
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    newsList: List<UserNewsResource>,
    pinnedNewsList: List<UserNewsResource>,
    onDetailClick: (String) -> Unit,
) {
    val analyticsHelper = LocalAnalyticsHelper.current

    LazyVerticalGrid(
        state = gridState,
        modifier = modifier.animateContentSize(),
        columns = GridCells.Adaptive(300.dp),
    ) {
        if (pinnedNewsList.isNotEmpty()) {
            item(key = "contentHeader", span = { GridItemSpan(maxLineSpan) }) {
                PinnedNewsScreen(
                    pages = pinnedNewsList,
                    onDetailClick = onDetailClick
                )
            }
        }

        if (newsList.isNotEmpty()) {
            item(key = "newsSubHeader", span = { GridItemSpan(maxLineSpan) }) {
                NewsSubHeader()
            }
            items(items = newsList, key = { news -> news.id }) { news ->
                NewsRow(
                    modifier = Modifier.fillMaxWidth(),
                    news = news,
                    onDetailClick = {
                        analyticsHelper.logNewsResourceOpened(newsId = news.id)
                        onDetailClick(news.id)
                    }
                )
            }
        }
    }
}