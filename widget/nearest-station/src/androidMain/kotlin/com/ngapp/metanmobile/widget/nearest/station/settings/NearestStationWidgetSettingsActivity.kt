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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.glance.GlanceId
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.widget.core.settings.WidgetAppearanceSettings
import com.ngapp.metanmobile.widget.core.settings.WidgetChoiceRow
import com.ngapp.metanmobile.widget.core.settings.WidgetPreview
import com.ngapp.metanmobile.widget.core.settings.WidgetPreviewCaption
import com.ngapp.metanmobile.widget.core.settings.WidgetPreviewColors
import com.ngapp.metanmobile.widget.core.settings.WidgetPreviewStatusDot
import com.ngapp.metanmobile.widget.core.settings.WidgetPreviewTile
import com.ngapp.metanmobile.widget.core.settings.WidgetPreviewValue
import com.ngapp.metanmobile.widget.core.settings.WidgetSettingsActivity
import com.ngapp.metanmobile.widget.core.settings.WidgetSettingsHint
import com.ngapp.metanmobile.widget.core.settings.WidgetSettingsLayout
import com.ngapp.metanmobile.widget.core.theme.WidgetStatus
import com.ngapp.metanmobile.widget.nearest.station.state.NearestStationTiles
import dev.icerock.moko.resources.compose.stringResource

class NearestStationWidgetSettingsActivity : WidgetSettingsActivity<NearestStationWidgetSettings>() {

    override suspend fun loadSettings(context: Context, glanceId: GlanceId) =
        loadNearestStationWidgetSettings(context, glanceId)

    override suspend fun saveSettings(
        context: Context,
        glanceId: GlanceId,
        settings: NearestStationWidgetSettings,
    ) = saveNearestStationWidgetSettings(context, glanceId, settings)

    @Composable
    override fun SettingsScreen(
        initialSettings: NearestStationWidgetSettings,
        onSave: (NearestStationWidgetSettings) -> Unit,
    ) {
        var settings by remember { mutableStateOf(initialSettings) }
        val tiles = settings.tiles
        WidgetSettingsLayout(
            onSave = { onSave(settings) },
            preview = {
                // The widget at its default (wide) size, exactly as the chosen options draw it.
                WidgetPreview(
                    appearance = settings.appearance,
                    halfWidth = tiles != NearestStationTiles.BOTH,
                ) { colors ->
                    if (tiles.showsPrice) PriceTilePreview(colors)
                    if (tiles.showsStation) StationTilePreview(colors)
                }
            },
        ) {
            WidgetChoiceRow(
                title = SharedRes.strings.widget_nearest_station_settings_text_show,
                options = NearestStationTiles.entries,
                selected = tiles,
                optionTitle = {
                    when (it) {
                        NearestStationTiles.PRICE -> SharedRes.strings.widget_nearest_station_settings_show_price
                        NearestStationTiles.DISTANCE -> SharedRes.strings.widget_nearest_station_settings_show_station
                        NearestStationTiles.BOTH -> SharedRes.strings.widget_nearest_station_settings_show_both
                    }
                },
                onSelect = { settings = settings.copy(tiles = it) },
            )
            WidgetSettingsHint(SharedRes.strings.widget_nearest_station_settings_hint_resize)
            WidgetAppearanceSettings(
                appearance = settings.appearance,
                onAppearanceChange = { settings = settings.copy(appearance = it) },
            )
        }
    }
}

@Composable
private fun RowScope.PriceTilePreview(colors: WidgetPreviewColors) {
    WidgetPreviewTile(colors) {
        WidgetPreviewValue(stringResource(SharedRes.strings.core_ui_text_value_byn, "1,16"), colors)
        WidgetPreviewCaption(stringResource(SharedRes.strings.core_ui_text_cng_price), colors)
    }
}

@Composable
private fun RowScope.StationTilePreview(colors: WidgetPreviewColors) {
    WidgetPreviewTile(colors) {
        WidgetPreviewValue(
            text = stringResource(SharedRes.strings.core_ui_text_value_km, "2,4"),
            colors = colors,
            trailing = { WidgetPreviewStatusDot(WidgetStatus.OK, colors) },
        )
        WidgetPreviewCaption(stringResource(SharedRes.strings.core_ui_text_nearest_station), colors)
    }
}
