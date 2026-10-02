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
import com.ngapp.metanmobile.widget.nearest.station.state.NearestStationWidgetUiState
import com.ngapp.metanmobile.widget.nearest.station.state.formatDistanceKm
import com.ngapp.metanmobile.widget.nearest.station.state.toNearestStationWidgetUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NearestStationWidgetUiStateTest {

    @Test
    fun nothingSyncedYetIsNoData() {
        val uiState = WidgetData(cngPrice = null, location = null, stations = emptyList())
            .toNearestStationWidgetUiState()

        assertEquals(NearestStationWidgetUiState.NoData, uiState)
    }

    @Test
    fun unknownLocationKeepsThePriceButNoStation() {
        val uiState = widgetData(station("a", distance = null)).toNearestStationWidgetUiState()

        uiState as NearestStationWidgetUiState.Success
        assertEquals("1.06", uiState.cngPrice?.content)
        assertNull(uiState.nearestStation)
    }

    @Test
    fun picksTheClosestStation() {
        val uiState = widgetData(
            station("far", distance = 12.0),
            station("near", distance = 2.4),
        ).toNearestStationWidgetUiState()

        assertEquals("near", (uiState as NearestStationWidgetUiState.Success).nearestStation?.code)
    }

    @Test
    fun skipsServiceInfrastructure() {
        val uiState = widgetData(
            station("service", distance = 0.5, type = StationType.SERVICE.typeName),
            station("cng", distance = 3.0),
        ).toNearestStationWidgetUiState()

        assertEquals("cng", (uiState as NearestStationWidgetUiState.Success).nearestStation?.code)
    }

    @Test
    fun distanceIsFormattedLikeOnHome() {
        assertEquals("2,4", formatDistanceKm(2.36))
        assertEquals("0,0", formatDistanceKm(0.0))
        assertEquals("12,0", formatDistanceKm(11.98))
    }

    private fun widgetData(vararg stations: UserStationResource) = WidgetData(
        cngPrice = PriceResource.init().copy(content = "1.06"),
        location = null,
        stations = stations.toList(),
    )

    private fun station(
        code: String,
        distance: Double?,
        type: String = StationType.CNG.typeName,
    ) = UserStationResource.init().copy(code = code, type = type, distanceBetween = distance)
}
