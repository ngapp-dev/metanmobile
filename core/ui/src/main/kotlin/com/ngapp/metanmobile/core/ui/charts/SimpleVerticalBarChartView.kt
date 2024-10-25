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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.Red
import com.ngapp.metanmobile.core.designsystem.theme.textColor
import kotlinx.coroutines.delay

data class VerticalBarChartItem(
    val id: Int,
    val active: Boolean,
    val title: String,
    val value: Float,
)

@Composable
fun SimpleVerticalBarChartView(
    modifier: Modifier = Modifier,
    data: List<VerticalBarChartItem>,
    barCornersRadius: Float = 12f,
    barColor: Color = Blue,
    activeBarColor: Color = Red,
    barWidth: Float = 20f,
    labelOffset: Float = 30f,
    labelColor: Color = MMColors.textColor,
) {
    var screenSize by remember { mutableStateOf(Size.Zero) }
    val chosenBar by remember { mutableIntStateOf(-1) }
    var chosenBarKey by remember { mutableStateOf("") }
    LaunchedEffect(chosenBar) {
        delay(3000)
        chosenBarKey = ""
    }
    Canvas(modifier = modifier
        .fillMaxSize()
        .padding(
            top = 16.dp,
            bottom = 12.dp,
            start = 18.dp,
            end = 18.dp
        ),
        onDraw = {
            screenSize = size
            val spaceBetweenBars = (size.width - (data.size * barWidth)) / (data.size - 1)
            val maxBarHeight = data.maxOf { it.value }
            val barScale = size.height / maxBarHeight
            val paint = Paint().apply {
                this.color = labelColor.toArgb()
                textAlign = Paint.Align.CENTER
                textSize = 30f
            }

            var spaceStep = 0f

            for (item in data) {
                val topLeft = Offset(
                    x = spaceStep,
                    y = size.height - item.value * barScale - labelOffset
                )
                //--------------------(draw bars)--------------------//
                drawRoundRect(
                    color = if (item.active) {
                        activeBarColor
                    } else {
                        barColor
                    },
                    topLeft = topLeft,
                    size = Size(
                        width = barWidth,
                        height = size.height - topLeft.y - labelOffset
                    ),
                    cornerRadius = CornerRadius(barCornersRadius, barCornersRadius)
                )
                //--------------------(showing the x axis labels)--------------------//
                drawContext.canvas.nativeCanvas.drawText(
                    item.title,
                    spaceStep + barWidth / 2,
                    10f + size.height,
                    paint
                )
                spaceStep += spaceBetweenBars + barWidth
            }
        })
}


private fun detectPosition(screenSize: Size, offset: Offset, listSize: Int, itemWidth: Float): Int {
    val spaceBetweenBars =
        (screenSize.width - (listSize * itemWidth)) / (listSize - 1)
    var spaceStep = 0f
    for (i in 0 until listSize) {
        if (offset.x in spaceStep..(spaceStep + itemWidth)) {
            return i
        }
        spaceStep += spaceBetweenBars + itemWidth
    }
    return -1
}
