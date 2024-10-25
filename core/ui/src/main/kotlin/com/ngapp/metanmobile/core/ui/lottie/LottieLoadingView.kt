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

package com.ngapp.metanmobile.core.ui.lottie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.util.Delayed

@Composable
fun LottieLoadingView(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    delayMillis: Long = 100L,
) {
    Delayed(delayMillis = delayMillis) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = when (modifier == Modifier) {
                true -> Modifier
                    .fillMaxSize()
                    .padding(paddingValues)

                false -> modifier.padding(paddingValues)
            }
        ) {
            LottieView(
                file = "loading.json",
                modifier = modifier.height(72.dp)
            )
        }
    }
}