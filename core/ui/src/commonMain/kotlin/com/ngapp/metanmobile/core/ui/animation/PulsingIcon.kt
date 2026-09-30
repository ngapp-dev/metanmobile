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

package com.ngapp.metanmobile.core.ui.animation

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A plain Compose "breathing" icon (gentle scale + alpha pulse), used in place of the Lottie/kottie
 * JSON animations that used to play in the empty/error states. `kottie` was the only multiplatform
 * path to Lottie (`lottie-compose` itself is Android-only) — dropping it removes a JSON-animation
 * dependency from both Android and iOS in one place, in favor of a plain `infiniteRepeatable`
 * animation that needs no bundled asset.
 */
@Composable
fun PulsingIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    tint: Color = LocalContentColor.current,
) {
    val transition = rememberInfiniteTransition(label = "pulsing icon transition")
    val scale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulsing icon scale",
    )
    val alpha by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulsing icon alpha",
    )
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            },
    )
}
