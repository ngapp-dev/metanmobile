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

package com.ngapp.metanmobile.core.ui.charts

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.Red
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.designsystem.theme.textColor
import kotlinx.coroutines.delay

data class HorizontalBarChartItem(
    val id: Int,
    val active: Boolean,
    val title: String,
    val value: Float,
)

@Composable
fun SimpleHorizontalBarChartView(
    modifier: Modifier = Modifier,
    data: List<HorizontalBarChartItem>,
    barCornersRadius: Float = 12f,
    barColor: Color = Blue,
    activeBarColor: Color = Red,
    barHeight: Float = 52f,
    height: Dp,
    titleColor: Color = MMColors.textColor,
    valueColor: Color = White,
    backgroundColor: Color = Color.Transparent,
    topStartRadius: Dp = 0.dp,
    topEndRadius: Dp = 0.dp,
    bottomStartRadius: Dp = 0.dp,
    bottomEndRadius: Dp = 0.dp,
) {
    val shape = RoundedCornerShape(
        topStart = topStartRadius,
        topEnd = topEndRadius,
        bottomEnd = bottomEndRadius,
        bottomStart = bottomStartRadius
    )
    var screenSize by remember { mutableStateOf(Size.Zero) }

    val chosenBar by remember { mutableIntStateOf(-1) }
    var chosenBarKey by remember { mutableStateOf("") }

    LaunchedEffect(chosenBar) {
        delay(3000)
        chosenBarKey = ""
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape = shape)
            .background(
                color = backgroundColor,
                shape = shape
            )
            .animateContentSize()
    ) {
        Canvas(modifier = modifier
            .fillMaxSize()
            .padding(all = 8.dp),
            onDraw = {
                screenSize = size
                val spaceBetweenBars = (size.height - (data.size * barHeight)) / (data.size - 1)
                val maxBarWidth = data.maxOf { it.value }
                val barScale = size.width / maxBarWidth
                val paintTitle = Paint().apply {
                    this.color = titleColor.toArgb()
                    textAlign = Paint.Align.LEFT
                    textSize = 32f
                }
                val paintValue = Paint().apply {
                    this.color = valueColor.toArgb()
                    textAlign = Paint.Align.CENTER
                    textSize = 32f
                    typeface = Typeface.DEFAULT_BOLD
                }

                var spaceStep = 0f

                data.forEach { item ->
                    val topLeft = Offset(
                        x = size.width - item.value * barScale,
                        y = spaceStep
                    )
                    //--------------------(showing the y axis titles)--------------------//
                    drawContext.canvas.nativeCanvas.drawText(
                        item.title,
                        0f,
                        spaceStep - barHeight / 2,
                        paintTitle,
                    )
                    //--------------------(draw bars)--------------------//
                    drawRoundRect(
                        color = if (item.active) {
                            activeBarColor
                        } else {
                            barColor
                        },
                        topLeft = Offset(0f, spaceStep),
                        size = Size(
                            width = size.width - topLeft.x,
                            height = barHeight
                        ),
                        cornerRadius = CornerRadius(barCornersRadius, barCornersRadius)
                    )
                    //--------------------(showing the y axis values)--------------------//
                    drawContext.canvas.nativeCanvas.drawText(
                        "${item.value.toInt()} КМ",
                        (size.width - topLeft.x) / 2,
                        spaceStep + (barHeight / 2 + 12f),
                        paintValue,
                    )
                    spaceStep += spaceBetweenBars + barHeight
                }
            })
    }
}