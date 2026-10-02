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
import com.ngapp.metanmobile.core.designsystem.theme.BackgroundDark
import com.ngapp.metanmobile.core.designsystem.theme.BackgroundLight
import com.ngapp.metanmobile.core.designsystem.theme.Black
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.widget.core.appearance.WidgetAppearance
import com.ngapp.metanmobile.widget.core.appearance.WidgetColor
import com.ngapp.metanmobile.widget.core.appearance.WidgetGlass
import com.ngapp.metanmobile.widget.core.appearance.WidgetSurfaceStyle
import com.ngapp.metanmobile.widget.core.appearance.WidgetThemeMode
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WidgetPaletteTest {

    @Test
    fun `the defaults look like the app - light in the light theme, dark in the dark one`() {
        val palette = WidgetAppearance().palette()

        assertEquals(DayNightColor(day = White, night = Black), palette.container.fill)
        assertEquals(DayNightColor(day = BackgroundLight, night = BackgroundDark), palette.tile.fill)
        assertEquals(DayNightColor(day = Black, night = White), palette.onTile)
        assertFalse(palette.container.isGlass)
        assertFalse(palette.tile.isGlass)
    }

    @Test
    fun `a fixed theme pins every AUTO color to that side`() {
        val light = WidgetAppearance(themeMode = WidgetThemeMode.LIGHT).palette()
        val dark = WidgetAppearance(themeMode = WidgetThemeMode.DARK).palette()

        assertEquals(DayNightColor(White), light.container.fill)
        assertEquals(DayNightColor(Black), light.onTile)
        assertEquals(DayNightColor(Black), dark.container.fill)
        assertEquals(DayNightColor(White), dark.onTile)
    }

    @Test
    fun `an explicit color ignores the theme`() {
        val appearance = WidgetAppearance(
            themeMode = WidgetThemeMode.DARK,
            background = WidgetSurfaceStyle(color = WidgetColor.WHITE),
        )

        assertEquals(DayNightColor(White), appearance.palette().container.fill)
    }

    @Test
    fun `opacity becomes the fill's alpha`() {
        val palette = WidgetAppearance(
            background = WidgetSurfaceStyle(color = WidgetColor.BLUE, opacity = 0),
            tiles = WidgetSurfaceStyle(color = WidgetColor.BLUE, opacity = 50),
        ).palette()

        assertEquals(0f, palette.container.fill.day.alpha)
        assertEquals(0.5f, palette.tile.fill.day.alpha, absoluteTolerance = 0.01f)
    }

    @Test
    fun `glass replaces the color with its tint at the chosen strength`() {
        val palette = WidgetAppearance(
            background = WidgetSurfaceStyle(
                color = WidgetColor.BLACK,
                glass = WidgetGlass(enabled = true, tint = WidgetColor.BLUE, tintStrength = 30),
            ),
        ).palette()

        assertTrue(palette.container.isGlass)
        assertEquals(Blue.copy(alpha = 0.3f), palette.container.fill.day)
    }

    @Test
    fun `text is white on dark tiles and dark on white ones`() {
        fun onTile(color: WidgetColor) =
            WidgetAppearance(tiles = WidgetSurfaceStyle(color = color)).palette().onTile

        assertEquals(DayNightColor(White), onTile(WidgetColor.BLACK))
        assertEquals(DayNightColor(White), onTile(WidgetColor.BLUE))
        assertEquals(DayNightColor(Black), onTile(WidgetColor.WHITE))
    }

    @Test
    fun `see-through tiles take their text color from what shows through them`() {
        val palette = WidgetAppearance(
            background = WidgetSurfaceStyle(color = WidgetColor.WHITE),
            tiles = WidgetSurfaceStyle(color = WidgetColor.BLACK, opacity = 10),
        ).palette()

        assertEquals(DayNightColor(Black), palette.onTile)
    }

    @Test
    fun `the operating dot gets a ring on blue tiles only`() {
        val blueTiles = WidgetAppearance(tiles = WidgetSurfaceStyle(color = WidgetColor.BLUE)).palette()
        val whiteTiles = WidgetAppearance(tiles = WidgetSurfaceStyle(color = WidgetColor.WHITE)).palette()

        assertNotNull(blueTiles.statusDotRing(WidgetStatus.OK))
        assertEquals(blueTiles.onTile, blueTiles.statusDotRing(WidgetStatus.OK))
        assertNull(blueTiles.statusDotRing(WidgetStatus.PROBLEM))
        assertNull(whiteTiles.statusDotRing(WidgetStatus.OK))
    }

    @Test
    fun `isNight follows the phone only in the system theme`() {
        assertTrue(WidgetThemeMode.SYSTEM.isNight(isSystemNight = true))
        assertFalse(WidgetThemeMode.SYSTEM.isNight(isSystemNight = false))
        assertFalse(WidgetThemeMode.LIGHT.isNight(isSystemNight = true))
        assertTrue(WidgetThemeMode.DARK.isNight(isSystemNight = false))
    }

    @Test
    fun `transparent stays transparent`() {
        val palette = WidgetAppearance(background = WidgetSurfaceStyle(opacity = 0)).palette()

        assertEquals(0f, palette.container.fill.day.alpha)
        assertEquals(0f, palette.container.fill.night.alpha)
        assertEquals(Color.Transparent.alpha, palette.container.fill.night.alpha)
    }
}
