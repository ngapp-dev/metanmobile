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

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.ngapp.metanmobile.widget.core.appearance.WidgetAppearance
import com.ngapp.metanmobile.widget.core.appearance.enumValueOrNull
import com.ngapp.metanmobile.widget.core.appearance.toWidgetAppearance
import com.ngapp.metanmobile.widget.core.appearance.writeWidgetAppearance
import com.ngapp.metanmobile.widget.nearest.station.state.NearestStationTiles

/** Everything the user can set on one placed "nearest station" widget. */
data class NearestStationWidgetSettings(
    val appearance: WidgetAppearance = WidgetAppearance(),
    val tiles: NearestStationTiles = NearestStationTiles.BOTH,
)

// Lives next to the shared appearance keys in the widget's own Glance state.
private val TilesKey = stringPreferencesKey("nearest_station_tiles")

fun Preferences.toNearestStationWidgetSettings() = NearestStationWidgetSettings(
    appearance = toWidgetAppearance(),
    tiles = enumValueOrNull<NearestStationTiles>(this[TilesKey]) ?: NearestStationTiles.BOTH,
)

suspend fun loadNearestStationWidgetSettings(context: Context, glanceId: GlanceId) =
    getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
        .toNearestStationWidgetSettings()

suspend fun saveNearestStationWidgetSettings(
    context: Context,
    glanceId: GlanceId,
    settings: NearestStationWidgetSettings,
) {
    updateAppWidgetState(context, glanceId) { it.writeNearestStationWidgetSettings(settings) }
}

/** The write half of [toNearestStationWidgetSettings]. */
fun MutablePreferences.writeNearestStationWidgetSettings(settings: NearestStationWidgetSettings) {
    writeWidgetAppearance(settings.appearance)
    this[TilesKey] = settings.tiles.name
}
