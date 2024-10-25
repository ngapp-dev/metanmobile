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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.designsystem.component.ButtonWithIcon
import com.ngapp.metanmobile.core.designsystem.component.MMTextButton
import com.ngapp.metanmobile.core.designsystem.component.htmltext.HtmlText
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMShapes
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.Red700
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.ui.R
import com.ngapp.metanmobile.core.ui.logCareerResourceOpened

@Composable
fun CareerRow(
    career: CareerResource,
    cardElevation: Dp = 0.dp,
) {
    val uriHandler = LocalUriHandler.current
    val analyticsHelper = LocalAnalyticsHelper.current
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        shape = MMShapes.large,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = career.title,
                style = MaterialTheme.typography.displayMedium,
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.padding(top = 8.dp)) {
                CareerWidgetIconWithText(
                    subtitleResId = R.string.core_ui_title_workplace,
                    text = career.place.ifEmpty { stringResource(R.string.core_ui_text_place_any) },
                    icon = {
                        Image(
                            imageVector = ImageVector.vectorResource(MMIcons.StationCng),
                            contentDescription = career.place,
                            modifier = Modifier.height(24.dp),
                        )
                    }
                )
                Spacer(Modifier.width(16.dp))
                CareerWidgetIconWithText(
                    subtitleResId = R.string.core_ui_title_experience,
                    text = career.exp.ifEmpty { stringResource(R.string.core_ui_text_no_experience) },
                    icon = {
                        Icon(
                            imageVector = MMIcons.Exp,
                            contentDescription = career.exp,
                            tint = Red700,
                            modifier = Modifier.height(24.dp),
                        )
                    }
                )
            }
            if (career.description.isNotEmpty()) {
                CareerWidgetSubTitle(R.string.core_ui_title_description)
                CareerWidgetInfoContent(career.description)
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column {
                    if (career.requirements.isNotEmpty()) {
                        CareerWidgetSubTitle(R.string.core_ui_title_requirements)
                        CareerWidgetInfoContent(career.requirements)
                    }
                    if (career.responsibilities.isNotEmpty()) {
                        CareerWidgetSubTitle(R.string.core_ui_title_responsibilities)
                        CareerWidgetInfoContent(career.responsibilities)
                    }
                }
            }
            if (career.requirements.isNotEmpty() || career.responsibilities.isNotEmpty()) {
                MMTextButton(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = { isExpanded = !isExpanded },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                    ),
                    text = {
                        Text(
                            text = if (isExpanded) {
                                stringResource(R.string.core_ui_button_show_less)
                            } else {
                                stringResource(R.string.core_ui_button_show_more)
                            },
                            style = MMTypography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    trailingIcon = {
                        val iconRotateState by animateFloatAsState(
                            targetValue = if (isExpanded) 180f else 0f, label = "arrowAnimation"
                        )
                        Icon(
                            modifier = Modifier.rotate(iconRotateState),
                            imageVector = MMIcons.ExpandMore,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = if (isExpanded) {
                                stringResource(R.string.core_ui_button_show_less)
                            } else {
                                stringResource(R.string.core_ui_button_show_more)
                            },
                        )
                    }
                )
            }
            ButtonWithIcon(
                icon = MMIcons.Email,
                iconTint = White,
                textResId = R.string.core_ui_button_career_apply,
                buttonBackgroundColor = Blue,
                fontColor = White,
                borderStrokeColor = Blue,
                modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    analyticsHelper.logCareerResourceOpened(career.title)
                    uriHandler.openUri("mailto:ok.ecogas@btg.by?subject=${career.title}")
                }
            )
        }
    }
}

@Composable
private fun CareerWidgetSubTitle(subtitleResId: Int) {
    Text(
        text = stringResource(subtitleResId),
        style = MMTypography.headlineMedium,
        color = Gray400,
        modifier = Modifier.padding(top = 12.dp)
    )
}

@Composable
private fun CareerWidgetInfoContent(text: String) {
    HtmlText(
        text = text,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
        style = MMTypography.titleLarge,
        lineHeight = 22.sp,
    )
}

@Composable
private fun CareerWidgetIconWithText(
    icon: @Composable () -> Unit,
    subtitleResId: Int,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Text(
            text = stringResource(subtitleResId),
            color = Gray400,
            style = MMTypography.headlineMedium,
            modifier = Modifier.padding(start = 4.dp),
        )
        Text(
            text = text,
            style = MMTypography.titleLarge,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}