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

package com.ngapp.metanmobile.feature.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.component.MMTextButton
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Green
import com.ngapp.metanmobile.core.designsystem.theme.MMShapes
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.ui.faq.FaqRow
import com.ngapp.metanmobile.core.ui.faq.FaqRowShimmer
import com.ngapp.metanmobile.core.ui.R as CoreUiR

@Composable
internal fun HomeWidgetFaqView(
    isEditingUi: Boolean,
    pinnedFaqItems: List<FaqResource>,
    onSeeAllClick: () -> Unit,
    reorderableItemModifier: Modifier = Modifier,
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Green)
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(id = CoreUiR.string.core_ui_text_faq),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = White,
                style = MMTypography.displayLarge,
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = MMShapes.large)
                    .background(MaterialTheme.colorScheme.onBackground)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (pinnedFaqItems.isNotEmpty()) {
                        pinnedFaqItems.forEachIndexed { i, faq ->
                            FaqRow(faqItem = faq)
                            if (i < pinnedFaqItems.size - 1) {
                                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    } else {
                        Column(modifier = Modifier.padding(vertical = 12.dp)) {
                            FaqRowShimmer()
                            MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            FaqRowShimmer()
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            MMTextButton(
                onClick = onSeeAllClick,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                ),
                text = {
                    Text(
                        text = stringResource(id = CoreUiR.string.core_ui_button_show_all).uppercase(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        style = MMTypography.displaySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
        AnimatedVisibility(
            visible = isEditingUi,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd),
        ) {
            IconButton(
                modifier = reorderableItemModifier.padding(start = 8.dp, top = 8.dp),
                onClick = {},
            ) {
                Icon(
                    imageVector = MMIcons.DragHandle,
                    contentDescription = stringResource(CoreUiR.string.core_ui_description_reorder_drag_handle_icon),
                    tint = White
                )
            }
        }
    }
}
