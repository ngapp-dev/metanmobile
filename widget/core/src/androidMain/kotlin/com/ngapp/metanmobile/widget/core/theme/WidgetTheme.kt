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

package com.ngapp.metanmobile.widget.core.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.color.ColorProvider
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.ngapp.metanmobile.core.designsystem.theme.BackgroundDark
import com.ngapp.metanmobile.core.designsystem.theme.BackgroundLight
import com.ngapp.metanmobile.core.designsystem.theme.Black
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.Red
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.widget.core.appearance.WIDGET_PERCENT_MAX
import com.ngapp.metanmobile.widget.core.appearance.WidgetAppearance
import com.ngapp.metanmobile.widget.core.appearance.WidgetColor
import com.ngapp.metanmobile.widget.core.appearance.WidgetSurfaceStyle
import com.ngapp.metanmobile.widget.core.appearance.WidgetThemeMode

/*
 * The app's design tokens translated for Glance, which can't use the Compose design system
 * components themselves. Colors come from the user's WidgetAppearance; AUTO follows the system
 * light/dark theme, like the rest of the home screen does. Also used by the settings preview,
 * so the preview and the widget can't drift apart.
 */

/** A color for the system light theme and for the dark one. */
data class DayNightColor(val day: Color, val night: Color) {
    constructor(color: Color) : this(color, color)

    val provider: ColorProvider get() = ColorProvider(day = day, night = night)

    fun resolve(isNight: Boolean): Color = if (isNight) night else day

    /** Pinned to one side when the user fixed the widget's theme; as is for SYSTEM. */
    fun forTheme(mode: WidgetThemeMode): DayNightColor = when (mode) {
        WidgetThemeMode.SYSTEM -> this
        WidgetThemeMode.LIGHT -> DayNightColor(day)
        WidgetThemeMode.DARK -> DayNightColor(night)
    }
}

data class WidgetSurfacePaint(
    val fill: DayNightColor,
    /** Draw the glass highlight and rim over the fill (see WidgetSurfaces). */
    val isGlass: Boolean,
)

data class WidgetPalette(
    val container: WidgetSurfacePaint,
    val tile: WidgetSurfacePaint,
    val onTile: DayNightColor,
    /** The swatch the tiles mostly look like, to keep the status dot visible on them. */
    val tileLooksLike: WidgetColor,
)

/** The swatch's color in the light and the dark system theme. */
private fun WidgetColor.base(auto: DayNightColor): DayNightColor = when (this) {
    WidgetColor.BLACK -> DayNightColor(Black)
    WidgetColor.WHITE -> DayNightColor(White)
    WidgetColor.BLUE -> DayNightColor(Blue)
    WidgetColor.AUTO -> auto
}

/** Readable text on a surface of this color. */
private val WidgetColor.content: DayNightColor
    get() = when (this) {
        WidgetColor.WHITE -> DayNightColor(Black)
        WidgetColor.BLACK, WidgetColor.BLUE -> DayNightColor(White)
        WidgetColor.AUTO -> DayNightColor(day = Black, night = White)
    }

private fun DayNightColor.withPercent(percent: Int): DayNightColor {
    val alpha = percent.coerceIn(0, WIDGET_PERCENT_MAX) / WIDGET_PERCENT_MAX.toFloat()
    return DayNightColor(day = day.copy(alpha = day.alpha * alpha), night = night.copy(alpha = night.alpha * alpha))
}

private const val HALF = WIDGET_PERCENT_MAX / 2

// AUTO keeps the app's look: a white/dark container with tiles a shade off it, like cards.
private val AutoContainer = DayNightColor(day = White, night = Black)
private val AutoTile = DayNightColor(day = BackgroundLight, night = BackgroundDark)

private fun WidgetSurfaceStyle.paint(auto: DayNightColor) = if (glass.enabled) {
    WidgetSurfacePaint(fill = glass.tint.base(auto).withPercent(glass.tintStrength), isGlass = true)
} else {
    WidgetSurfacePaint(fill = color.base(auto).withPercent(opacity), isGlass = false)
}

/** The color that dominates the surface; null when it's mostly see-through. */
private val WidgetSurfaceStyle.looksLike: WidgetColor?
    get() = when {
        glass.enabled -> glass.tint.takeIf { glass.tintStrength >= HALF }
        opacity >= HALF -> color
        else -> null
    }

fun WidgetAppearance.palette(): WidgetPalette {
    // Text sits on the tile, or on whatever shows through it: the background, then the
    // wallpaper, which is mostly dark behind see-through widgets — white reads best there.
    val tileLooksLike = tiles.looksLike ?: background.looksLike ?: WidgetColor.BLACK
    val container = background.paint(AutoContainer)
    val tile = tiles.paint(AutoTile)
    return WidgetPalette(
        container = container.copy(fill = container.fill.forTheme(themeMode)),
        tile = tile.copy(fill = tile.fill.forTheme(themeMode)),
        onTile = tileLooksLike.content.forTheme(themeMode),
        tileLooksLike = tileLooksLike,
    )
}

/** Whether the widget draws its dark side, given the phone's own dark mode. */
fun WidgetThemeMode.isNight(isSystemNight: Boolean): Boolean = when (this) {
    WidgetThemeMode.SYSTEM -> isSystemNight
    WidgetThemeMode.LIGHT -> false
    WidgetThemeMode.DARK -> true
}

/** Mirrors StationStatusView in core:ui. */
enum class WidgetStatus(val color: Color, val swatch: WidgetColor?) {
    OK(Blue, WidgetColor.BLUE),
    PROBLEM(Red, null),
    UNKNOWN(Gray400, null),
}

/**
 * A rim around the status dot, in the tile's text color, when the dot would otherwise
 * disappear into a tile of its own color (a blue "operating" dot on a blue tile).
 */
fun WidgetPalette.statusDotRing(status: WidgetStatus): DayNightColor? =
    onTile.takeIf { status.swatch != null && status.swatch == tileLooksLike }

object WidgetTextStyles {
    // MMTypography.displayMedium
    fun value(color: ColorProvider) =
        TextStyle(color = color, fontSize = 21.sp, fontWeight = FontWeight.Bold)

    // MMTypography.headlineMedium
    fun caption(color: ColorProvider) =
        TextStyle(color = color, fontSize = 15.sp, fontWeight = FontWeight.Medium)
}

object WidgetDimens {
    val containerPadding = 12.dp
    // Matches res/drawable/widget_glass_container.xml (close to launchers' widget radius).
    val containerCornerRadius = 24.dp
    val tileSpacing = 8.dp
    val tilePaddingHorizontal = 12.dp
    val tilePaddingVertical = 8.dp
    // MMShapes.large; matches res/drawable/widget_glass_tile.xml.
    val tileCornerRadius = 12.dp
    val statusDotSize = 6.dp
    val statusDotRingWidth = 2.dp
}
