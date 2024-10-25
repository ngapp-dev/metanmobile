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

package com.ngapp.metanmobile.feature.contacts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.ui.ShimmerEffect

@Composable
internal fun ContactsShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShimmerEffect(
            modifier = Modifier
                .height(15.dp)
                .width(150.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
        )
        ShimmerEffect(
            modifier = Modifier
                .height(15.dp)
                .width(150.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
        )
        ShimmerEffect(
            modifier = Modifier
                .height(15.dp)
                .width(200.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
        )
        ShimmerEffect(
            modifier = Modifier
                .height(15.dp)
                .width(250.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
        )
        ShimmerEffect(
            modifier = Modifier
                .height(15.dp)
                .width(200.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
        )
    }
}