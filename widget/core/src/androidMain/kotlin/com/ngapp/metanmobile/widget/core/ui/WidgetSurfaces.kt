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

package com.ngapp.metanmobile.widget.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.unit.ColorProvider
import com.ngapp.metanmobile.widget.core.R
import com.ngapp.metanmobile.widget.core.theme.WidgetDimens
import com.ngapp.metanmobile.widget.core.theme.WidgetPalette
import com.ngapp.metanmobile.widget.core.theme.WidgetStatus
import com.ngapp.metanmobile.widget.core.theme.WidgetSurfacePaint
import com.ngapp.metanmobile.widget.core.theme.statusDotRing


// The surfaces every widget is built from, so appearance settings (glass included) apply to
// all widgets the same way.

/**
 * The widget's background. An app can't resize its placed widget (only the launcher can), so a
 * widget with less to show sets [halfWidth]: it draws itself in the start half and leaves the
 * other half fully transparent. [modifier] goes on the visible part (e.g. a click action).
 */
@Composable
fun WidgetContainer(
    palette: WidgetPalette,
    modifier: GlanceModifier = GlanceModifier,
    halfWidth: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Two equal weights split the width exactly in half, whatever the launcher's padding.
    Row(modifier = GlanceModifier.fillMaxSize().appWidgetBackground()) {
        Surface(
            paint = palette.container,
            glassDrawable = R.drawable.widget_glass_container,
            modifier = modifier
                .defaultWeight()
                .fillMaxHeight()
                .cornerRadius(WidgetDimens.containerCornerRadius),
        ) {
            Column(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(WidgetDimens.containerPadding),
                content = content,
            )
        }
        if (halfWidth) Spacer(GlanceModifier.defaultWeight())
    }
}

/** One card inside the container: a value and its captions. */
@Composable
fun WidgetTile(
    palette: WidgetPalette,
    modifier: GlanceModifier = GlanceModifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        paint = palette.tile,
        glassDrawable = R.drawable.widget_glass_tile,
        modifier = modifier.cornerRadius(WidgetDimens.tileCornerRadius),
    ) {
        Column(
            modifier = GlanceModifier.padding(
                horizontal = WidgetDimens.tilePaddingHorizontal,
                vertical = WidgetDimens.tilePaddingVertical,
            ),
            content = content,
        )
    }
}

/** A status dot (see StationStatusView in core:ui), ringed when it would blend into the tile. */
@Composable
fun WidgetStatusDot(status: WidgetStatus, palette: WidgetPalette) {
    val ring = palette.statusDotRing(status)
    val ringWidth = WidgetDimens.statusDotRingWidth
    Spacer(GlanceModifier.width(6.dp - if (ring != null) ringWidth else 0.dp))
    val dot = GlanceModifier
        .size(WidgetDimens.statusDotSize)
        .cornerRadius(WidgetDimens.statusDotSize / 2)
        .background(ColorProvider(status.color))
    if (ring == null) {
        Box(dot) {}
    } else {
        val ringSize = WidgetDimens.statusDotSize + ringWidth * 2
        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier
                .size(ringSize)
                .cornerRadius(ringSize / 2)
                .background(ring.provider),
        ) {
            Box(dot) {}
        }
    }
}

// The glass highlight fills the surface, which takes its size from the content drawn on top.
@Composable
private fun Surface(
    paint: WidgetSurfacePaint,
    glassDrawable: Int,
    modifier: GlanceModifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.background(paint.fill.provider)) {
        if (paint.isGlass) {
            Box(GlanceModifier.fillMaxSize().background(ImageProvider(glassDrawable))) {}
        }
        content()
    }
}
