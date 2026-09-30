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

package com.ngapp.metanmobile.feature.news.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.ui.ItemDetailImageView

@Composable
internal fun NewsDetailContent(
    modifier: Modifier = Modifier,
    news: NewsResource,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
    ) {
        item("detailImage") {
            // The image starts right below the status bar (the glass buttons still float over
            // it), with its top edge fading in from the background instead of a hard line.
            val scrimColor = MaterialTheme.colorScheme.background
            Column {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Box {
                    ItemDetailImageView(imageUrl = news.detailPicture)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                Brush.verticalGradient(
                                    0f to scrimColor,
                                    1f to Color.Transparent,
                                ),
                            ),
                    )
                }
            }
        }
        item("title") {
            NewsDetailHeader(news = news)
        }
        item("contentInfo") {
            NewsDetailBody(content = news.content)
        }
    }
}
