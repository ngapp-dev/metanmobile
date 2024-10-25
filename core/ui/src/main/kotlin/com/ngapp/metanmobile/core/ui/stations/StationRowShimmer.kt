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

package com.ngapp.metanmobile.core.ui.stations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ngapp.metanmobile.core.designsystem.component.ProgressIndicatorSmall
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Gray500
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.ui.R
import com.ngapp.metanmobile.core.ui.ShimmerEffect
import java.util.Locale

@Composable
fun StationRowShimmer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(MMColors.cardBackgroundColor)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerEffect(
            modifier = Modifier
                .size(82.dp)
                .padding(vertical = 8.dp)
                .padding(end = 8.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(20.dp, 0.dp, 20.dp, 0.dp))
                .clip(RoundedCornerShape(20.dp, 0.dp, 20.dp, 0.dp)),
        )
        Column(
            modifier = modifier
                .weight(1f)
                .fillMaxSize()
                .padding(all = 4.dp)
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .width(150.dp)
                    .height(15.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            )
            ShimmerEffect(
                modifier = Modifier
                    .width(200.dp)
                    .height(14.dp)
                    .padding(top = 2.dp, bottom = 2.dp, end = 8.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            )
            ShimmerEffect(
                modifier = Modifier
                    .width(80.dp)
                    .height(12.dp)
                    .padding(top = 2.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            )
        }
    }
}