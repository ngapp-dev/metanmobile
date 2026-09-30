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

package com.ngapp.metanmobile.core.ui.career

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.theme.MMShapes
import com.ngapp.metanmobile.core.ui.ShimmerEffect

@Composable
fun CareerRowShimmer(cardElevation: Dp = 0.dp) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        shape = MMShapes.large,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ShimmerEffect(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.padding(top = 8.dp)) {
                ShimmerEffect(
                    modifier = Modifier
                        .height(15.dp)
                        .width(80.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(16.dp))
                ShimmerEffect(
                    modifier = Modifier
                        .height(15.dp)
                        .width(80.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                )
            }
            ShimmerEffect(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(15.dp)
                    .width(50.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            )
            ShimmerEffect(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 4.dp)
                    .height(22.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            )
        }
    }
}