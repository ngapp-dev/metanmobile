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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.component.MMTextButton
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.ui.logNewsResourceOpened
import com.ngapp.metanmobile.core.ui.news.NewsRow
import com.ngapp.metanmobile.core.ui.news.NewsRowShimmer
import com.ngapp.metanmobile.core.ui.news.PinnedNewsScreen
import com.ngapp.metanmobile.core.ui.R as CoreUiR

@Composable
internal fun HomeHeaderView(
    lastNewsItems: List<UserNewsResource>,
    pinnedNews: List<UserNewsResource>,
    onShowAllNewsClick: () -> Unit = {},
    onNewsDetailClick: (String) -> Unit = {},
) {
    val analyticsHelper = LocalAnalyticsHelper.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MMColors.cardBackgroundColor)
    ) {
        if (pinnedNews.isNotEmpty()) {
            PinnedNewsScreen(pinnedNews, onNewsDetailClick)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(CoreUiR.string.core_ui_title_latest_news),
                style = MMTypography.displayMedium,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            )
            MMTextButton(
                onClick = onShowAllNewsClick,
                text = {
                    Text(
                        text = stringResource(id = CoreUiR.string.core_ui_button_show_all),
                        textAlign = TextAlign.End,
                        style = MMTypography.headlineMedium,
                        color = Gray400
                    )
                }
            )
        }
        if (lastNewsItems.isNotEmpty()) {
            lastNewsItems.forEachIndexed { i, news ->
                NewsRow(
                    news = news,
                    titleMaxLines = 1,
                    imageSize = 64.dp,
                    imageRoundedCornerShape = RoundedCornerShape(12.dp, 0.dp, 12.dp, 0.dp),
                    onDetailClick = {
                        analyticsHelper.logNewsResourceOpened(newsId = news.id)
                        onNewsDetailClick(news.id)
                    }
                )
                if (i < lastNewsItems.size - 1) {
                    MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        } else {
            repeat(3) {
                NewsRowShimmer(
                    imageSize = 64.dp,
                    imageRoundedCornerShape = RoundedCornerShape(12.dp, 0.dp, 12.dp, 0.dp),
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}