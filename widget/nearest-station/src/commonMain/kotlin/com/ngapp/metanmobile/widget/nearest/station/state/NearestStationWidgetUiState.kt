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

package com.ngapp.metanmobile.widget.nearest.station.state

import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.StationType
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.widget.WidgetData
import kotlin.math.roundToInt

sealed interface NearestStationWidgetUiState {
    /** Nothing synced yet: the app was installed but hasn't loaded its data. */
    data object NoData : NearestStationWidgetUiState

    /** [nearestStation] is null while the user's location is unknown. */
    data class Success(
        val cngPrice: PriceResource?,
        val nearestStation: UserStationResource?,
    ) : NearestStationWidgetUiState
}

fun WidgetData.toNearestStationWidgetUiState(): NearestStationWidgetUiState {
    if (cngPrice == null && stations.isEmpty()) return NearestStationWidgetUiState.NoData
    return NearestStationWidgetUiState.Success(
        cngPrice = cngPrice,
        // Service infrastructure isn't somewhere to refuel, and refueling is what this widget is for.
        nearestStation = stations
            .filter { it.type != StationType.SERVICE.typeName }
            .filter { it.distanceBetween != null }
            .minByOrNull { it.distanceBetween!! },
    )
}

/** Same "2,4" format as the nearest-station card on the Home screen. */
fun formatDistanceKm(km: Double): String {
    val tenths = (km * 10).roundToInt()
    return "${tenths / 10},${tenths % 10}"
}
