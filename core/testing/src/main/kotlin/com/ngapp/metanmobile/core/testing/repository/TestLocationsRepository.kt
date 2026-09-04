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

import android.location.Location
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.model.location.LocationResource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestLocationsRepository : LocationsRepository {

    /**
     * The backing hot flow for the list of locations ids for testing.
     *
     * Seeded with an empty list so [getLocationResource] emits `null` immediately to the first
     * collector, matching [com.ngapp.metanmobile.core.data.repository.location.OfflineFirstLocationsRepository],
     * whose Room-backed flow emits right away even from an empty table. Without this seed, a
     * `MutableSharedFlow(replay = 1)` emits nothing at all until [sendLocationResources] is
     * called, which silently stalls every collector downstream (e.g. station lists combined with
     * location) at "no value yet" instead of the documented "location unknown" `null`.
     */
    private val locationResourcesFlow =
        MutableSharedFlow<List<LocationResource>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
            tryEmit(emptyList())
        }

    /**
     * A test-only API to allow controlling the list of location resources from tests.
     */
    fun sendLocationResources(locationResources: List<LocationResource>) {
        locationResourcesFlow.tryEmit(locationResources)
    }

    override fun getLocationResources(): Flow<List<LocationResource>> = locationResourcesFlow

    override fun getLocationResource(): Flow<LocationResource?> =
        locationResourcesFlow.map { locationResources -> locationResources.firstOrNull() }

    override suspend fun getLocationData(): Location = Location("test")

    /**
     * The permission values passed to successive [updateLocation] calls, in order, for
     * verifying delegation from tests.
     */
    val updateLocationCalls: List<Boolean> get() = _updateLocationCalls
    private val _updateLocationCalls = mutableListOf<Boolean>()

    override suspend fun updateLocation(locationPermissionGranted: Boolean) {
        _updateLocationCalls += locationPermissionGranted
    }
}
