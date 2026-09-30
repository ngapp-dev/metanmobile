/*
 * Copyright 2026 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop

/**
 * Bottom space that the floating navigation bar overlaps on top-level screens. Scrollable content
 * adds it to its `contentPadding` so the last items can scroll out from under the bar instead of
 * staying hidden behind it; it's 0.dp wherever the bar isn't shown (rail layout, detail screens).
 */
val LocalMMFloatingBarPadding = staticCompositionLocalOf { 0.dp }

/** Height of the floating bar itself, excluding [FloatingBarMargin]. */
internal val FloatingBarHeight = 64.dp

/** Gap between the floating bar and whatever sits below it (ad strip / screen edge). */
internal val FloatingBarMargin = 12.dp

private val FloatingBarShape = RoundedCornerShape(percent = 50)

/** A single destination rendered by [MMFloatingNavigationBar]. */
internal class MMNavigationItem(
    val selected: Boolean,
    val onClick: () -> Unit,
    val modifier: Modifier,
    val icon: @Composable () -> Unit,
    val selectedIcon: @Composable () -> Unit,
    val label: @Composable (() -> Unit)?,
)

/**
 * Telegram-style floating capsule navigation bar drawn as "liquid glass": the content behind it
 * ([backdrop]) is blurred, saturated and refracted towards the rounded edges, with a specular rim
 * highlight on top. The selected-item pill slides between destinations with a spring.
 *
 * Blur needs Android 12+ and refraction Android 13+ (always available on iOS); below that the
 * effects silently no-op and [glassTint] turns nearly opaque to keep labels readable.
 */
@Composable
internal fun MMFloatingNavigationBar(
    backdrop: Backdrop,
    items: List<MMNavigationItem>,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    val indicatorColor = MMNavigationDefaults.navigationIndicatorColor()
    val selectedIndex = items.indexOfFirst { it.selected }

    BoxWithConstraints(
        modifier = modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .height(FloatingBarHeight)
            .glass(backdrop = backdrop, shape = FloatingBarShape, tint = glassTint()),
    ) {
        val itemWidth = maxWidth / items.size
        if (selectedIndex >= 0) {
            val indicatorOffset by animateDpAsState(
                targetValue = itemWidth * selectedIndex,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
                label = "floatingBarIndicatorOffset",
            )
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(itemWidth)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .clip(FloatingBarShape)
                    .background(indicatorColor),
            )
        }
        Row(modifier = Modifier.fillMaxSize()) {
            items.forEach { item ->
                FloatingNavigationBarItem(item = item, width = itemWidth)
            }
        }
    }
}

@Composable
private fun FloatingNavigationBarItem(item: MMNavigationItem, width: Dp) {
    val contentColor by animateColorAsState(
        targetValue = if (item.selected) {
            MMNavigationDefaults.navigationSelectedItemColor()
        } else {
            MMNavigationDefaults.navigationContentColor()
        },
        label = "floatingBarItemColor",
    )
    Column(
        modifier = item.modifier
            .width(width)
            .fillMaxHeight()
            .clip(FloatingBarShape)
            .selectable(
                selected = item.selected,
                onClick = item.onClick,
                role = Role.Tab,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            if (item.selected) item.selectedIcon() else item.icon()
            item.label?.let { label ->
                ProvideTextStyle(
                    MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = if (item.selected) FontWeight.SemiBold else FontWeight.Medium,
                    ),
                ) {
                    Box(modifier = Modifier.padding(top = 2.dp)) { label() }
                }
            }
        }
    }
}
