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

import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import org.junit.Test
import kotlin.test.assertEquals

class WidgetAppearanceStateTest {

    @Test
    fun `a widget nobody configured gets the defaults`() {
        assertEquals(WidgetAppearance(), mutablePreferencesOf().toWidgetAppearance())
    }

    @Test
    fun `what is written is read back`() {
        val appearance = WidgetAppearance(
            themeMode = WidgetThemeMode.DARK,
            background = WidgetSurfaceStyle(
                color = WidgetColor.BLUE,
                opacity = 40,
                glass = WidgetGlass(enabled = true, tint = WidgetColor.BLACK, tintStrength = 65),
            ),
            tiles = WidgetSurfaceStyle(color = WidgetColor.WHITE, opacity = 80),
        )

        val preferences = mutablePreferencesOf().apply { writeWidgetAppearance(appearance) }

        assertEquals(appearance, preferences.toWidgetAppearance())
    }

    @Test
    fun `a value saved by an older version that no longer exists falls back to the default`() {
        val preferences = mutablePreferencesOf(
            stringPreferencesKey("widget_background_color") to "PURPLE",
            stringPreferencesKey("widget_theme_mode") to "SEPIA",
        )

        val appearance = preferences.toWidgetAppearance()

        assertEquals(WidgetColor.AUTO, appearance.background.color)
        assertEquals(WidgetThemeMode.SYSTEM, appearance.themeMode)
    }
}
