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

package com.ngapp.metanmobile.widget.nearest.station

import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.model.widget.WidgetData
import com.ngapp.metanmobile.widget.core.AppGroupWidgetFiles
import com.ngapp.metanmobile.widget.core.WidgetReloader
import com.ngapp.metanmobile.widget.core.WidgetUpdater
import com.ngapp.metanmobile.widget.nearest.station.snapshot.NearestStationWidgetSnapshot
import com.ngapp.metanmobile.widget.nearest.station.snapshot.NearestStationWidgetStrings
import com.ngapp.metanmobile.widget.nearest.station.snapshot.toNearestStationWidgetSnapshot
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.serialization.json.Json

/** Read by NearestStationProvider.swift in the widget extension. */
internal const val NEAREST_STATION_SNAPSHOT_FILE = "nearest-station.json"

/**
 * The iOS counterpart of the Android NearestStationWidgetUpdater: writes the snapshot into the
 * App Group and asks WidgetKit to redraw. WidgetKit rations redraws, so both only happen when
 * the snapshot actually changed.
 */
internal class NearestStationWidgetSnapshotWriter(
    private val files: AppGroupWidgetFiles,
    private val reloader: WidgetReloader,
) : WidgetUpdater {

    private var lastWritten: String? = null

    override suspend fun update(data: WidgetData) {
        val json = Json.encodeToString(data.toNearestStationWidgetSnapshot(localizedStrings()))
        if (json == lastWritten) return
        if (files.write(NEAREST_STATION_SNAPSHOT_FILE, json)) {
            lastWritten = json
            reloader.reloadAllWidgets()
        }
    }
}

// In the app's current language, so the widget speaks the same one.
private fun localizedStrings() = NearestStationWidgetStrings(
    priceFormat = { SharedRes.strings.core_ui_text_value_byn.formatted(it) },
    distanceFormat = { SharedRes.strings.core_ui_text_value_km.formatted(it) },
    texts = NearestStationWidgetSnapshot.Texts(
        cngPrice = SharedRes.strings.core_ui_text_cng_price.localized(),
        perCubicMeter = SharedRes.strings.core_ui_text_for_one_meter.localized(),
        nearestStation = SharedRes.strings.core_ui_text_nearest_station.localized(),
        noLocation = SharedRes.strings.widget_nearest_station_text_no_location.localized(),
        noData = SharedRes.strings.widget_text_no_data.localized(),
    ),
)

private fun StringResource.localized(): String = StringDesc.Resource(this).localized()

private fun StringResource.formatted(value: String): String =
    StringDesc.ResourceFormatted(this, value).localized()
