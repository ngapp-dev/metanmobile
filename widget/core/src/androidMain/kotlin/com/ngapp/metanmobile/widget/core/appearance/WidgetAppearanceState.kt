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

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.state.updateAppWidgetState

// Stored in each widget's own Glance state (keyed by its id), not in the app's user data: two
// copies of the same widget can look different.
private const val BACKGROUND = "widget_background"
private const val TILES = "widget_tiles"
private val ThemeModeKey = stringPreferencesKey("widget_theme_mode")

fun Preferences.toWidgetAppearance() = WidgetAppearance(
    themeMode = enumValueOrNull<WidgetThemeMode>(this[ThemeModeKey]) ?: WidgetThemeMode.SYSTEM,
    background = readSurfaceStyle(BACKGROUND),
    tiles = readSurfaceStyle(TILES),
)

suspend fun saveWidgetAppearance(context: Context, glanceId: GlanceId, appearance: WidgetAppearance) {
    updateAppWidgetState(context, glanceId) { it.writeWidgetAppearance(appearance) }
}

/** The write half of [toWidgetAppearance], for widgets that store more settings alongside. */
fun MutablePreferences.writeWidgetAppearance(appearance: WidgetAppearance) {
    this[ThemeModeKey] = appearance.themeMode.name
    writeSurfaceStyle(BACKGROUND, appearance.background)
    writeSurfaceStyle(TILES, appearance.tiles)
}

private class SurfaceKeys(prefix: String) {
    val color = stringPreferencesKey("${prefix}_color")
    val opacity = intPreferencesKey("${prefix}_opacity")
    val glassEnabled = booleanPreferencesKey("${prefix}_glass_enabled")
    val glassTint = stringPreferencesKey("${prefix}_glass_tint")
    val glassTintStrength = intPreferencesKey("${prefix}_glass_tint_strength")
}

private fun Preferences.readSurfaceStyle(prefix: String): WidgetSurfaceStyle {
    val keys = SurfaceKeys(prefix)
    val defaults = WidgetSurfaceStyle()
    return WidgetSurfaceStyle(
        color = enumValueOrNull<WidgetColor>(this[keys.color]) ?: defaults.color,
        opacity = this[keys.opacity] ?: defaults.opacity,
        glass = WidgetGlass(
            enabled = this[keys.glassEnabled] ?: defaults.glass.enabled,
            tint = enumValueOrNull<WidgetColor>(this[keys.glassTint]) ?: defaults.glass.tint,
            tintStrength = this[keys.glassTintStrength] ?: defaults.glass.tintStrength,
        ),
    )
}

private fun MutablePreferences.writeSurfaceStyle(prefix: String, style: WidgetSurfaceStyle) {
    val keys = SurfaceKeys(prefix)
    this[keys.color] = style.color.name
    this[keys.opacity] = style.opacity
    this[keys.glassEnabled] = style.glass.enabled
    this[keys.glassTint] = style.glass.tint.name
    this[keys.glassTintStrength] = style.glass.tintStrength
}

/** Reads an enum saved by name; a name from an older app version that no longer exists is null. */
inline fun <reified T : Enum<T>> enumValueOrNull(name: String?): T? =
    enumValues<T>().firstOrNull { it.name == name }
