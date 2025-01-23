/*
 * Copyright 2025 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.core.ui.stations

import android.net.Uri
import android.webkit.URLUtil
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.component.MMTextButton
import com.ngapp.metanmobile.core.designsystem.component.htmltext.HtmlText
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Green
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.R
import com.ngapp.metanmobile.core.ui.charts.SimpleVerticalBarChartView
import com.ngapp.metanmobile.core.ui.charts.createCharItems
import com.ngapp.metanmobile.core.ui.util.launchCustomChromeTab
import com.ngapp.metanmobile.core.ui.util.showClipboardToast
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun StationInfoRow(
    modifier: Modifier = Modifier,
    isExpandable: Boolean,
    rowIcon: ImageVector,
    text: String,
    url: String = "",
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val iconRotateState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f, label = "arrowAnimation"
    )
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(isExpandable) { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp),
        ) {
            Icon(
                imageVector = rowIcon,
                contentDescription = text,
                tint = Blue,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(24.dp),
            )
            Spacer(modifier = Modifier.width(32.dp))
            Text(
                text = text,
                style = MMTypography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onLongClick = {
                            clipboardManager.setText(AnnotatedString(text))
                            context.showClipboardToast()
                        },
                        onClick = {
                            if (url.isEmpty()) {
                                isExpanded = !isExpanded
                            } else {
                                if (URLUtil.isValidUrl(url)) {
                                    launchCustomChromeTab(context, Uri.parse(url), backgroundColor)
                                }
                            }
                        }
                    )
                    .padding(vertical = 16.dp)
            )
            if (isExpandable) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = MMIcons.ExpandMore,
                    contentDescription = if (isExpanded) {
                        stringResource(R.string.core_ui_button_show_less)
                    } else {
                        stringResource(R.string.core_ui_button_show_more)
                    },
                    tint = MMColors.onSurfaceVariant,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(24.dp)
                        .rotate(iconRotateState)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        MMDivider()
    }
}

@Composable
fun StationWorkTimeRow(
    rowIcon: ImageVector,
    workingTime: String,
    modifier: Modifier = Modifier,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val iconRotateState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f, label = "arrowAnimation"
    )
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val is24Hours = workingTime.contains("круглосуточно")
    val displayText =
        if (is24Hours) stringResource(R.string.core_ui_text_open_24_hours) else workingTime

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp),
        ) {
            Icon(
                imageVector = rowIcon,
                contentDescription = workingTime,
                tint = Blue,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(24.dp),
            )
            Spacer(modifier = Modifier.width(32.dp))
            HtmlText(
                text = if (isExpanded) workingTime else displayText,
                style = if (isExpanded) MMTypography.bodyLarge else MMTypography.titleLarge,
                color = if (isExpanded) Color.Unspecified else Green,
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onLongClick = {
                            clipboardManager.setText(AnnotatedString(workingTime))
                            context.showClipboardToast()
                        },
                        onClick = { isExpanded = !isExpanded }
                    )
                    .padding(vertical = 16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = MMIcons.ExpandMore,
                contentDescription = if (isExpanded) {
                    stringResource(R.string.core_ui_button_show_less)
                } else {
                    stringResource(R.string.core_ui_button_show_more)
                },
                tint = MMColors.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(24.dp)
                    .rotate(iconRotateState)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        MMDivider()
    }
}

@Composable
fun StationInfoListRow(
    modifier: Modifier = Modifier,
    isClickable: Boolean = true,
    rowIcon: ImageVector,
    text: String,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val list = text.split(",")

    Column {
        Row(modifier = modifier.fillMaxWidth()) {
            Icon(
                imageVector = rowIcon,
                contentDescription = text,
                tint = Blue,
                modifier = Modifier
                    .padding(top = 12.dp, start = 16.dp)
                    .size(24.dp),
            )
            Spacer(modifier = Modifier.width(32.dp))
            Column(Modifier.weight(1f)) {
                list.forEachIndexed { index, info ->
                    Text(
                        text = info.trim(),
                        style = MMTypography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                enabled = isClickable,
                                onLongClick = {
                                    clipboardManager.setText(AnnotatedString(text))
                                    context.showClipboardToast()
                                },
                                onClick = { uriHandler.openUri("tel:$info") }
                            )
                            .padding(vertical = 16.dp)
                            .padding(end = 16.dp)
                    )
                    if (index < list.size - 1) {
                        MMDivider()
                    }
                }
            }
        }
        MMDivider()
    }
}

@Composable
fun StationBusyHours(
    modifier: Modifier = Modifier,
    stationDetail: UserStationResource,
) {
    val coroutineScope = rememberCoroutineScope()
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var isPopupVisible by rememberSaveable { mutableStateOf(false) }

    val daysOfWeek = stringArrayResource(R.array.core_ui_text_on_day_of_week)
    val currentDayOfWeek = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
    val pagerState = rememberPagerState(
        pageCount = { daysOfWeek.size },
        initialPage = currentDayOfWeek,
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.core_ui_text_popular_times),
                style = MMTypography.titleLarge,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
                MMTextButton(
                    onClick = { isExpanded = !isExpanded },
                    text = {
                        Text(
                            text = daysOfWeek[pagerState.currentPage],
                            style = MMTypography.bodyLarge,
                        )
                    },
                    trailingIcon = {
                        val iconRotateState by animateFloatAsState(
                            targetValue = if (isExpanded) 180f else 0f, label = "arrowAnimation"
                        )
                        Icon(
                            imageVector = MMIcons.ExpandMore,
                            contentDescription = if (isExpanded) {
                                stringResource(R.string.core_ui_button_show_less)
                            } else {
                                stringResource(R.string.core_ui_button_show_more)
                            },
                            tint = MMColors.onSurfaceVariant,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(iconRotateState)
                        )
                    }
                )
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier
                        .width(200.dp)
                        .background(MMColors.surface)
                ) {
                    daysOfWeek.forEachIndexed { index, currentDay ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = currentDay,
                                        style = MMTypography.bodyLarge,
                                        color = MMColors.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                isExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { isPopupVisible = !isPopupVisible }) {
                Icon(
                    imageVector = MMIcons.QuestionFilled,
                    contentDescription = stringResource(R.string.core_ui_description_attendance),
                    tint = MMColors.inverseSurface,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        if (isPopupVisible) {
            Popup(
                alignment = Alignment.TopEnd,
                onDismissRequest = { isPopupVisible = false }
            ) {
                Box(modifier = Modifier.padding(top = 36.dp, end = 4.dp)) {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .shadow(8.dp)
                            .background(MMColors.surface)
                            .padding(16.dp)

                    ) {
                        Text(
                            text = stringResource(R.string.core_ui_text_based_on_visits),
                            style = MMTypography.bodyLarge
                        )
                    }
                }
            }
        }
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            state = pagerState,
            verticalAlignment = Alignment.Top,
        ) { selectedPage ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .height(height = 88.dp)
                        .widthIn(0.dp, 420.dp)
                ) {
                    SimpleVerticalBarChartView(
                        modifier = Modifier.align(Alignment.TopCenter),
                        data = createCharItems(currentDayOfWeek, selectedPage, stationDetail)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(daysOfWeek.size) { index ->
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) MMColors.onSurfaceVariant
                            else MMColors.inverseSurface
                        )
                )
            }
        }
        MMDivider()
    }
}
