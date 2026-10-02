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

package com.ngapp.metanmobile.widget.core.appearance

/**
 * How a placed widget looks, chosen per widget instance by the user. Shared by every widget,
 * so they all offer the same settings: one style for the widget's background, one for its tiles.
 */
data class WidgetAppearance(
    /** Decides what every AUTO color resolves to; explicit colors don't change with it. */
    val themeMode: WidgetThemeMode = WidgetThemeMode.SYSTEM,
    val background: WidgetSurfaceStyle = WidgetSurfaceStyle(),
    val tiles: WidgetSurfaceStyle = WidgetSurfaceStyle(),
)

/** SYSTEM follows the phone's dark mode; LIGHT and DARK keep the widget that way regardless. */
enum class WidgetThemeMode { SYSTEM, LIGHT, DARK }

/** A surface is either a plain color at some opacity, or liquid glass (which replaces both). */
data class WidgetSurfaceStyle(
    val color: WidgetColor = WidgetColor.AUTO,
    /** 0 = fully transparent, 100 = solid. */
    val opacity: Int = WIDGET_PERCENT_MAX,
    val glass: WidgetGlass = WidgetGlass(),
)

/**
 * Liquid glass, imitated: a tinted see-through fill with a highlight and a light rim. Android
 * widgets can't blur the wallpaper behind them (the launcher draws them from RemoteViews).
 */
data class WidgetGlass(
    val enabled: Boolean = false,
    val tint: WidgetColor = WidgetColor.WHITE,
    /** How much of [tint] the glass carries, 0..100. */
    val tintStrength: Int = 20,
)

/** The color swatches offered in the settings. AUTO = light in the light theme, dark in the dark. */
enum class WidgetColor { BLACK, WHITE, BLUE, AUTO }

const val WIDGET_PERCENT_MAX = 100
