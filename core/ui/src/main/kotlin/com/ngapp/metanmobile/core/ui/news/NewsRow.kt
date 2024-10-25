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

package com.ngapp.metanmobile.core.ui.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.BluePale
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.GreenPale
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.ui.R
import com.ngapp.metanmobile.core.ui.util.dateFormatted

@Composable
fun NewsRow(
    modifier: Modifier = Modifier,
    titleMaxLines: Int = 2,
    imageSize: Dp = 82.dp,
    imageRoundedCornerShape: RoundedCornerShape = RoundedCornerShape(20.dp, 0.dp, 20.dp, 0.dp),
    news: UserNewsResource,
    onDetailClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(
                brush = if (!news.hasBeenViewed) {
                    Brush.horizontalGradient(
                        colors = listOf(BluePale, BluePale, GreenPale)
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            MMColors.cardBackgroundColor,
                            MMColors.cardBackgroundColor,
                            MMColors.cardBackgroundColor,
                        )
                    )
                }
            )
            .clickable(onClick = onDetailClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(news.previewPicture)
                .crossfade(true)
                .build(),
            error = painterResource(MMIcons.Error),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(
                R.string.core_ui_description_news_list_img, news.title
            ),
            modifier = Modifier
                .size(imageSize)
                .padding(vertical = 8.dp)
                .padding(end = 8.dp)
                .clip(imageRoundedCornerShape)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = news.title,
                maxLines = titleMaxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 2.dp, end = 8.dp),
                style = MMTypography.titleLarge
            )
            Text(
                text = dateFormatted(news.dateCreated),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Gray400,
                style = MMTypography.headlineMedium
            )
        }
    }
}