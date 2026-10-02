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

import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.StationType
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.widget.WidgetData
import com.ngapp.metanmobile.widget.nearest.station.snapshot.NearestStationWidgetSnapshot
import com.ngapp.metanmobile.widget.nearest.station.snapshot.NearestStationWidgetSnapshot.Status
import com.ngapp.metanmobile.widget.nearest.station.snapshot.NearestStationWidgetStrings
import com.ngapp.metanmobile.widget.nearest.station.snapshot.toNearestStationWidgetSnapshot
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NearestStationWidgetSnapshotTest {

    private val strings = NearestStationWidgetStrings(
        priceFormat = { "$it BYN" },
        distanceFormat = { "$it km" },
        texts = NearestStationWidgetSnapshot.Texts(
            cngPrice = "CNG price",
            perCubicMeter = "For 1 m3",
            nearestStation = "Nearest station",
            noLocation = "No location",
            noData = "No data",
        ),
    )

    @Test
    fun theNearestStationAndPriceAreReadyToShow() {
        val snapshot = widgetData(
            station("far", distance = 9.0),
            station("near", distance = 2.36, isOperate = 0),
        ).toNearestStationWidgetSnapshot(strings)

        assertEquals("1.16 BYN", snapshot.price?.value)
        val station = snapshot.station!!
        assertEquals("near", station.code)
        assertEquals("2,4 km", station.distance)
        assertEquals(Status.NOT_OPERATING, station.status)
        assertEquals("metanmobile://ecogas-map/near/", station.url)
        assertEquals(strings.texts, snapshot.texts)
    }

    @Test
    fun serviceInfrastructureIsNeverTheNearestStation() {
        val snapshot = widgetData(
            station("service", distance = 0.5, type = StationType.SERVICE.typeName),
            station("cng", distance = 4.0),
        ).toNearestStationWidgetSnapshot(strings)

        assertEquals("cng", snapshot.station?.code)
    }

    @Test
    fun anUnknownLocationLeavesNoStation() {
        val snapshot = widgetData(station("a", distance = null)).toNearestStationWidgetSnapshot(strings)

        assertEquals("1.16 BYN", snapshot.price?.value)
        assertNull(snapshot.station)
    }

    @Test
    fun nothingSyncedYetLeavesNeitherPriceNorStation() {
        val snapshot = WidgetData(cngPrice = null, location = null, stations = emptyList())
            .toNearestStationWidgetSnapshot(strings)

        assertNull(snapshot.price)
        assertNull(snapshot.station)
    }

    /** The Swift widget decodes these exact names (NearestStationSnapshot.swift). */
    @Test
    fun theJsonKeepsTheNamesTheSwiftWidgetReads() {
        val json = Json.encodeToString(
            widgetData(station("a", distance = 1.0)).toNearestStationWidgetSnapshot(strings),
        )
        val root = Json.parseToJsonElement(json).jsonObject

        assertEquals(setOf("price", "station", "texts"), root.keys)
        assertEquals(setOf("value"), root["price"]!!.jsonObject.keys)
        assertEquals(setOf("code", "distance", "address", "status", "url"), root["station"]!!.jsonObject.keys)
        assertEquals(
            setOf("cngPrice", "perCubicMeter", "nearestStation", "noLocation", "noData"),
            root["texts"]!!.jsonObject.keys,
        )
        assertEquals("\"OPERATING\"", root["station"]!!.jsonObject["status"].toString())
    }

    private fun widgetData(vararg stations: UserStationResource) = WidgetData(
        cngPrice = PriceResource.init().copy(content = "1.16"),
        location = null,
        stations = stations.toList(),
    )

    private fun station(
        code: String,
        distance: Double?,
        isOperate: Int = 1,
        type: String = StationType.CNG.typeName,
    ) = UserStationResource.init().copy(code = code, distanceBetween = distance, isOperate = isOperate, type = type)
}
