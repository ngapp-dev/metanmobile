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

package com.ngapp.metanmobile.widget.nearest.station.settings

import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ngapp.metanmobile.widget.core.appearance.WidgetAppearance
import com.ngapp.metanmobile.widget.core.appearance.WidgetColor
import com.ngapp.metanmobile.widget.core.appearance.WidgetSurfaceStyle
import com.ngapp.metanmobile.widget.core.appearance.WidgetThemeMode
import com.ngapp.metanmobile.widget.nearest.station.state.NearestStationTiles
import org.junit.Test
import kotlin.test.assertEquals

class NearestStationWidgetSettingsTest {

    @Test
    fun `a widget nobody configured shows both tiles with the default look`() {
        assertEquals(NearestStationWidgetSettings(), mutablePreferencesOf().toNearestStationWidgetSettings())
        assertEquals(NearestStationTiles.BOTH, NearestStationWidgetSettings().tiles)
    }

    @Test
    fun `what is written is read back, appearance included`() {
        val settings = NearestStationWidgetSettings(
            appearance = WidgetAppearance(
                themeMode = WidgetThemeMode.LIGHT,
                tiles = WidgetSurfaceStyle(color = WidgetColor.BLUE, opacity = 70),
            ),
            tiles = NearestStationTiles.DISTANCE,
        )

        val preferences = mutablePreferencesOf().apply { writeNearestStationWidgetSettings(settings) }

        assertEquals(settings, preferences.toNearestStationWidgetSettings())
    }

    @Test
    fun `an unknown tiles value falls back to both`() {
        val preferences = mutablePreferencesOf(stringPreferencesKey("nearest_station_tiles") to "MAP")

        assertEquals(NearestStationTiles.BOTH, preferences.toNearestStationWidgetSettings().tiles)
    }
}
