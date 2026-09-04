/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.core.testing.repository

import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.station.StationResourceQuery
import com.ngapp.metanmobile.core.data.repository.station.StationsRepository
import com.ngapp.metanmobile.core.model.station.StationResource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class TestStationsRepository : StationsRepository {

    /**
     * The backing hot flow for the list of stations ids for testing.
     *
     * Seeded with an empty list so collectors get a value right away, the same as
     * `StationResourceDao`'s Room-backed flow emitting an empty list from an empty table instead
     * of nothing at all — see [TestLocationsRepository] for why an un-seeded
     * `MutableSharedFlow(replay = 1)` is a trap for anything that `combine`s this with another
     * flow.
     */
    private val stationResourcesFlow =
        MutableSharedFlow<List<StationResource>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
            tryEmit(emptyList())
        }

    /**
     * A test-only API to allow controlling the list of location resources from tests.
     */
    fun sendStationResources(stationResources: List<StationResource>) {
        stationResourcesFlow.tryEmit(stationResources)
    }

    override fun getStationResourcesAsc(query: StationResourceQuery): Flow<List<StationResource>> =
        stationResourcesFlow.map { stationResources -> applyQuery(stationResources, query) }

    override fun getStationResourcesDesc(query: StationResourceQuery): Flow<List<StationResource>> =
        stationResourcesFlow.map { stationResources -> applyQuery(stationResources, query) }

    /**
     * Mirrors the `WHERE` clause of `StationResourceDao.getStationResourcesAsc/Desc`: filters by
     * [StationResourceQuery.filterStationCodes] and does a case-insensitive substring match of
     * [StationResourceQuery.searchQuery] against the title (SQLite's `LIKE '%…%'` is
     * case-insensitive for ASCII).
     */
    private fun applyQuery(
        stationResources: List<StationResource>,
        query: StationResourceQuery,
    ): List<StationResource> {
        var result = stationResources
        query.filterStationCodes?.let { filterStationCodes ->
            result = result.filter { it.code in filterStationCodes }
        }
        if (query.searchQuery.isNotEmpty()) {
            result = result.filter { it.title.contains(query.searchQuery, ignoreCase = true) }
        }
        return result
    }

    /**
     * `mapNotNull` (not `.map { .first { ... } }`) so subscribing before [stationCode] has been
     * sent — e.g. against the initial empty-list seed — simply emits nothing yet instead of
     * throwing `NoSuchElementException`, matching a Room query that has no matching row so far.
     */
    override fun getStationResource(stationCode: String): Flow<StationResource> =
        stationResourcesFlow.mapNotNull { stationResources -> stationResources.find { it.code == stationCode } }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = true
}
