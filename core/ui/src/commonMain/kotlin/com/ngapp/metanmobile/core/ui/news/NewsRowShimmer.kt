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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.ui.ShimmerEffect

@Composable
fun NewsRowShimmer(
    modifier: Modifier = Modifier,
    imageSize: Dp = 82.dp,
    imageRoundedCornerShape: RoundedCornerShape = RoundedCornerShape(20.dp, 0.dp, 20.dp, 0.dp),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerEffect(
            modifier = Modifier
                .size(imageSize)
                .padding(vertical = 8.dp)
                .padding(end = 8.dp)
                .clip(imageRoundedCornerShape)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .width(150.dp)
                    .height(20.dp)
                    .padding(bottom = 2.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            )
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            )
        }
    }
}