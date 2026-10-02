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

package com.ngapp.metanmobile.core.data.repository.widget

import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.data.repository.price.PricesRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.model.widget.WidgetData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

class CompositeWidgetDataRepository(
    private val stationsRepository: StationResourcesWithFavoritesRepository,
    private val pricesRepository: PricesRepository,
    private val locationsRepository: LocationsRepository,
) : WidgetDataRepository {

    override fun observeWidgetData(): Flow<WidgetData> = combine(
        stationsRepository.observeAll(query = StationResourceQuery()),
        pricesRepository.getFuelPrice(),
        locationsRepository.getLocationResource(),
    ) { stations, cngPrice, location ->
        WidgetData(
            cngPrice = cngPrice,
            location = location,
            stations = stations,
        )
    }.distinctUntilChanged()
}
