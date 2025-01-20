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

package com.ngapp.metanmobile.feature.stations.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngapp.metanmobile.core.common.util.fromStringToListFloat
import com.ngapp.metanmobile.core.designsystem.component.htmltext.HtmlText
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMShapes
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.designsystem.theme.smallWidgetBackgroundColor
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.charts.SimpleVerticalBarChartView
import com.ngapp.metanmobile.core.ui.charts.VerticalBarChartItem
import com.ngapp.metanmobile.feature.stations.R
import java.util.Calendar
import java.util.Locale
import com.ngapp.metanmobile.core.ui.R as CoreUiR

@Composable
internal fun StationDetailBodyView(
    station: UserStationResource,
    cngPrice: PriceResource?,
) {
    val cngPriceText = stringResource(
        id = CoreUiR.string.core_ui_text_value_byn, cngPrice?.content ?: ""
    ) + " " + stringResource(id = CoreUiR.string.core_ui_text_for_one_meter)

    val distanceBetween = stringResource(
        id = CoreUiR.string.core_ui_text_value_km,
        String.format(Locale.getDefault(), "%.1f", station.distanceBetween).replace('.', ',')
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MMColors.cardBackgroundColor)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            StationDetailSubTitle(CoreUiR.string.core_ui_text_station_hours)
            HtmlText(
                text = station.workingTime,
                modifier = Modifier.padding(top = 4.dp),
                style = MMTypography.titleLarge,
                lineHeight = 22.sp
            )

            StationDetailSubTitle(CoreUiR.string.core_ui_text_station_address)
            StationDetailInfoContent(station.address)
            StationDetailInfoContent(distanceBetween)

            StationDetailSubTitle(CoreUiR.string.core_ui_text_station_payments)
            StationDetailInfoContent(station.payment)

            StationDetailSubTitle(CoreUiR.string.core_ui_text_station_phones)
            StationDetailPhones(station)

            StationDetailSubTitle(CoreUiR.string.core_ui_text_cng_price)
            StationDetailInfoContent(cngPriceText)
            StationDetailAttendanceChart(station = station)
        }
    }
}

@Composable
private fun StationDetailSubTitle(subtitleResId: Int) {
    Text(
        text = stringResource(subtitleResId),
        style = MMTypography.headlineMedium,
        color = Gray400,
        modifier = Modifier.padding(top = 12.dp)
    )
}

@Composable
private fun StationDetailInfoContent(stationContent: String?) {
    Text(
        text = stationContent ?: "",
        style = MMTypography.titleLarge,
        lineHeight = 22.sp,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun StationDetailPhones(station: UserStationResource) {
    val number = station.phones.split(",")
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        number.forEach { phone ->
            Text(
                text = phone.trim(),
                style = MMTypography.titleLarge,
                color = Blue,
                lineHeight = 20.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable(onClick = { uriHandler.openUri("tel:$phone") })
                    .padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun StationDetailAttendanceChart(station: UserStationResource) {
    if (createCharItems(station).isNotEmpty()) {
        StationDetailSubTitle(R.string.feature_stations_text_station_attendance)
        Card(
            modifier = Modifier.padding(vertical = 5.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = MMShapes.large
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .height(height = 88.dp)
                    .widthIn(0.dp, 420.dp)
                    .background(MMColors.smallWidgetBackgroundColor)
                    .clip(MMShapes.large)
            ) {
                SimpleVerticalBarChartView(data = createCharItems(station))
            }
        }
    }
}

private fun createCharItems(station: UserStationResource): List<VerticalBarChartItem> {
    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val attendanceString = when (currentDay) {
        Calendar.MONDAY -> station.busyOnMonday
        Calendar.TUESDAY -> station.busyOnTuesday
        Calendar.WEDNESDAY -> station.busyOnWednesday
        Calendar.THURSDAY -> station.busyOnThursday
        Calendar.FRIDAY -> station.busyOnFriday
        Calendar.SATURDAY -> station.busyOnSaturday
        Calendar.SUNDAY -> station.busyOnSunday
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
                            active = currentHour == index,
                            title = index.toString(),
                            value = attendanceList[index] ?: 0f
                        )
                    }

                    else -> {
                        VerticalBarChartItem(
                            id = index,
                            active = currentHour == index,
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