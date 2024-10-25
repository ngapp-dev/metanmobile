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

package com.ngapp.metanmobile.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.util.Delayed

object ProgressIndicatorDefaults {
    val sizeLarge = 32.dp to 2.dp
    val sizeMedium = 24.dp to 1.5.dp
    val sizeSmall = 16.dp to 1.dp
    val size = 48.dp to 4.dp
}

@Composable
fun ProgressIndicatorSmall(modifier: Modifier = Modifier) =
    ProgressIndicator(
        modifier,
        ProgressIndicatorDefaults.sizeSmall.first,
        ProgressIndicatorDefaults.sizeSmall.second
    )

@Composable
fun ProgressIndicatorMedium(modifier: Modifier = Modifier) =
    ProgressIndicator(
        modifier,
        ProgressIndicatorDefaults.sizeMedium.first,
        ProgressIndicatorDefaults.sizeMedium.second
    )

@Composable
fun ProgressIndicator(modifier: Modifier = Modifier) =
    ProgressIndicator(
        modifier,
        ProgressIndicatorDefaults.sizeLarge.first,
        ProgressIndicatorDefaults.sizeLarge.second
    )

@Composable
fun ProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = ProgressIndicatorDefaults.size.first,
    strokeWidth: Dp = ProgressIndicatorDefaults.size.second,
    color: Color = MaterialTheme.colorScheme.secondary,
) {
    CircularProgressIndicator(modifier.size(size), color, strokeWidth)
}

private const val FULL_SCREEN_LOADING_DELAY = 100L

@Composable
fun FullScreenLoading(
    modifier: Modifier = Modifier,
    delayMillis: Long = FULL_SCREEN_LOADING_DELAY,
) {
    Delayed(delayMillis = delayMillis) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = when (modifier == Modifier) {
                true -> Modifier.fillMaxSize()
                false -> modifier
            }
        ) {
            ProgressIndicator()
        }
    }
}