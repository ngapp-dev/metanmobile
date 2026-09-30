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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import dev.icerock.moko.resources.compose.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.common.util.fromStringToListFloat
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Green
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.textColor
import com.ngapp.metanmobile.core.model.station.UserStationResource
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
    activeBarColor: Color = Green,
    barWidth: Float = 20f,
    labelOffset: Float = 30f,
    labelColor: Color = MMColors.textColor,
    axisXColor: Color = MMColors.inverseSurface,
) {
    var screenSize by remember { mutableStateOf(Size.Zero) }
    val chosenBar by remember { mutableIntStateOf(-1) }
    var chosenBarKey by remember { mutableStateOf("") }
    val busyNowText = stringResource(SharedRes.strings.core_ui_text_busy_now)
    val textMeasurer = rememberTextMeasurer()
    LaunchedEffect(chosenBar) {
        delay(3000)
        chosenBarKey = ""
    }
    // A day with no attendance data yields an empty `data` (see createCharItems) - draw nothing
    // rather than crash: data.maxOf{} below throws NoSuchElementException on an empty list, and
    // spaceBetweenBars' (data.size - 1) divisor is meaningless for fewer than 2 bars anyway.
    if (data.isEmpty()) return
    Canvas(modifier = modifier
        .fillMaxSize()
        .padding(
            top = 32.dp,
            bottom = 12.dp,
            start = 18.dp,
            end = 18.dp
        ),
        onDraw = {
            screenSize = size
            val spaceBetweenBars = (size.width - (data.size * barWidth)) / (data.size - 1)
            val maxBarHeight = data.maxOf { it.value }
            val barScale = size.height / maxBarHeight
            val axisLabelStyle = TextStyle(color = labelColor, fontSize = 15.sp)
            val barTextStyle = TextStyle(color = labelColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)

            val barTextLayout = textMeasurer.measure(busyNowText, barTextStyle)
            val barTextPaintWidth = barTextLayout.size.width.toFloat()
            val barTextPaintHeight = barTextLayout.size.height
            val barTextPadding = 20f
            var spaceStep = 0f

            for (item in data) {
                val topY = size.height - item.value * barScale - labelOffset
                val topLeft = Offset(
                    x = spaceStep,
                    y = topY
                )
                //-----------------(draw the x axis)-----------------//
                val xAxisY = size.height - labelOffset
                drawLine(
                    color = axisXColor,
                    start = Offset(-20f, xAxisY),
                    end = Offset(size.width + 20f, xAxisY),
                    strokeWidth = 1.dp.toPx()
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
                //--------------------(draw vertical line for active bar)--------------------//
                if (item.active) {
                    val dashLineYOffset = -80f
                    val centerX = spaceStep + barWidth / 2
                    val dashEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(10f, 10f),
                        phase = 0f,
                    )
                    drawLine(
                        color = axisXColor,
                        start = Offset(centerX, (dashLineYOffset + 15f)),
                        end = Offset(centerX, topY),
                        strokeWidth = 4f,
                        pathEffect = dashEffect,
                    )

                    drawPath(
                        path = getCloudPath(
                            barTextPaintWidth,
                            barTextPaintHeight,
                            centerX,
                            dashLineYOffset,
                            barTextPadding,
                        ),
                        color = axisXColor,
                        style = Stroke(width = 3f)
                    )
                    drawText(
                        textLayoutResult = barTextLayout,
                        topLeft = Offset(
                            centerX - barTextPaintWidth / 2f,
                            dashLineYOffset - barTextPaintHeight - barTextPadding / 2f,
                        ),
                    )
                }
                //--------------------(showing the x axis labels)--------------------//
                val axisLabelLayout = textMeasurer.measure(item.title, axisLabelStyle)
                drawText(
                    textLayoutResult = axisLabelLayout,
                    topLeft = Offset(
                        spaceStep + barWidth / 2 - axisLabelLayout.size.width / 2f,
                        size.height - axisLabelLayout.size.height / 2f,
                    ),
                )
                spaceStep += spaceBetweenBars + barWidth
            }
        })
}

private fun getCloudPath(
    textWidth: Float,
    textHeight: Int,
    centerX: Float,
    yOffset: Float,
    cloudPadding: Float,
): Path {
    val cloudWidth = textWidth + cloudPadding * 2
    val cloudHeight = textHeight + cloudPadding * 2
    val cornerRadius = 16f
    val arrowWidth = 20f
    val arrowHeight = 10f
    return Path().apply {
        moveTo(centerX - cloudWidth / 2 + cornerRadius, yOffset - cloudHeight)
        lineTo(centerX + cloudWidth / 2 - cornerRadius, yOffset - cloudHeight)
        arcTo(
            rect = Rect(
                centerX + cloudWidth / 2 - cornerRadius * 2,
                yOffset - cloudHeight,
                centerX + cloudWidth / 2,
                yOffset - cloudHeight + cornerRadius * 2
            ),
            startAngleDegrees = -90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(centerX + cloudWidth / 2, yOffset - cornerRadius)
        arcTo(
            rect = Rect(
                centerX + cloudWidth / 2 - cornerRadius * 2,
                yOffset - cornerRadius * 2,
                centerX + cloudWidth / 2,
                yOffset
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(centerX + arrowWidth / 2, yOffset)
        lineTo(centerX, yOffset + arrowHeight)
        lineTo(centerX - arrowWidth / 2, yOffset)
        lineTo(centerX - cloudWidth / 2 + cornerRadius, yOffset)
        arcTo(
            rect = Rect(
                centerX - cloudWidth / 2,
                yOffset - cornerRadius * 2,
                centerX - cloudWidth / 2 + cornerRadius * 2,
                yOffset
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(centerX - cloudWidth / 2, yOffset - cloudHeight + cornerRadius)
        arcTo(
            rect = Rect(
                centerX - cloudWidth / 2,
                yOffset - cloudHeight,
                centerX - cloudWidth / 2 + cornerRadius * 2,
                yOffset - cloudHeight + cornerRadius * 2
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        close()
    }
}

fun createCharItems(
    currentDayIndex: Int,
    selectedDayIndex: Int,
    station: UserStationResource,
): List<VerticalBarChartItem> {
    val currentHour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour

    val attendanceString = when (selectedDayIndex) {
        0 -> station.busyOnMonday
        1 -> station.busyOnTuesday
        2 -> station.busyOnWednesday
        3 -> station.busyOnThursday
        4 -> station.busyOnFriday
        5 -> station.busyOnSaturday
        6 -> station.busyOnSunday
        else -> ""
    }
    val readyCharItems = mutableListOf<VerticalBarChartItem>()
    attendanceString.let {
        val attendanceList: List<Float?> = fromStringToListFloat(it)
        if (attendanceList.isNotEmpty()) {
            (0..23).forEach { index ->
                readyCharItems += when (index) {
                    0, 3, 6, 9, 12, 15, 18, 21 -> {
                        VerticalBarChartItem(
                            id = index,
                            active = selectedDayIndex == currentDayIndex && currentHour == index,
                            title = index.toString(),
                            value = attendanceList[index] ?: 0f
                        )
                    }

                    else -> {
                        VerticalBarChartItem(
                            id = index,
                            active = selectedDayIndex == currentDayIndex && currentHour == index,
                            title = "",
                            value = attendanceList[index] ?: 0f
                        )
                    }
                }
            }
        }
    }
    return readyCharItems
}
