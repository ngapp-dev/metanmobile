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
import android.util.Log
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.model.location.LocationResource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestLocationsRepository : LocationsRepository {

    /**
     * The backing hot flow for the list of locations ids for testing.
     */
    private val locationResourcesFlow: MutableSharedFlow<List<LocationResource>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     * A test-only API to allow controlling the list of location resources from tests.
     */
    fun sendLocationResources(locationResources: List<LocationResource>) {
        locationResourcesFlow.tryEmit(locationResources)
    }

    override fun getLocationResources(): Flow<List<LocationResource>> = locationResourcesFlow

    override fun getLocationResource(): Flow<LocationResource> =
        locationResourcesFlow.map { locationResources ->
            if (locationResources.isNotEmpty()) {
                locationResources.first()
            } else {
                LocationResource.init()
            }
        }

    override suspend fun getLocationData(): Location = Location("test")

    override suspend fun updateLocation(locationPermissionGranted: Boolean) {
        Log.d("TestLocationsRepository", "updateLocation: $locationPermissionGranted")
    }
}
