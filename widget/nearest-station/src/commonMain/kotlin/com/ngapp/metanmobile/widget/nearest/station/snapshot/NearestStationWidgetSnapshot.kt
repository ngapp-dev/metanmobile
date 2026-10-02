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

package com.ngapp.metanmobile.widget.nearest.station.snapshot

import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.widget.WidgetData
import com.ngapp.metanmobile.widget.nearest.station.state.NearestStationWidgetUiState
import com.ngapp.metanmobile.widget.nearest.station.state.formatDistanceKm
import com.ngapp.metanmobile.widget.nearest.station.state.toNearestStationWidgetUiState
import kotlinx.serialization.Serializable

/**
 * What the iOS widget extension (SwiftUI, no Kotlin) draws, written by the app as JSON into the
 * App Group. The field names are a contract with NearestStationSnapshot.swift: change both
 * together. Texts come already translated into the app's language, and the nearest station is
 * already picked, so the Swift side only lays things out.
 */
@Serializable
data class NearestStationWidgetSnapshot(
    /** Null until the app has synced its data at least once. */
    val price: Price?,
    /** Null while the user's location is unknown. */
    val station: Station?,
    val texts: Texts,
) {
    @Serializable
    data class Price(
        /** "1.16 BYN" */
        val value: String,
    )

    @Serializable
    data class Station(
        val code: String,
        /** "2,4 km" */
        val distance: String,
        val address: String,
        val status: Status,
        /** Opens the station in the app (see MetanMobileAppState.navigateToDeepLink). */
        val url: String,
    )

    @Serializable
    enum class Status { OPERATING, NOT_OPERATING, UNKNOWN }

    @Serializable
    data class Texts(
        val cngPrice: String,
        val perCubicMeter: String,
        val nearestStation: String,
        val noLocation: String,
        val noData: String,
    )
}

/** The translated strings the snapshot needs; formats take the value as their only %s. */
data class NearestStationWidgetStrings(
    val priceFormat: (String) -> String,
    val distanceFormat: (String) -> String,
    val texts: NearestStationWidgetSnapshot.Texts,
)

/** iOS has no App Links without a hosted association file, so the app's custom scheme is used. */
private const val STATION_URL = "metanmobile://ecogas-map"

fun WidgetData.toNearestStationWidgetSnapshot(
    strings: NearestStationWidgetStrings,
): NearestStationWidgetSnapshot {
    val uiState = toNearestStationWidgetUiState()
    val success = uiState as? NearestStationWidgetUiState.Success
    return NearestStationWidgetSnapshot(
        price = success?.cngPrice?.let { NearestStationWidgetSnapshot.Price(strings.priceFormat(it.content)) },
        station = success?.nearestStation?.toSnapshotStation(strings),
        texts = strings.texts,
    )
}

private fun UserStationResource.toSnapshotStation(strings: NearestStationWidgetStrings) =
    NearestStationWidgetSnapshot.Station(
        code = code,
        distance = strings.distanceFormat(formatDistanceKm(distanceBetween ?: 0.0)),
        address = address,
        status = when (isOperate) {
            1 -> NearestStationWidgetSnapshot.Status.OPERATING
            0 -> NearestStationWidgetSnapshot.Status.NOT_OPERATING
            else -> NearestStationWidgetSnapshot.Status.UNKNOWN
        },
        url = "$STATION_URL/$code/",
    )
