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

package com.ngapp.metanmobile.core.data.repository.location

import com.ngapp.metanmobile.core.database.dao.location.LocationResourceDao
import com.ngapp.metanmobile.core.database.model.location.LocationResourceEntity
import com.ngapp.metanmobile.core.database.model.location.asExternalModel
import com.ngapp.metanmobile.core.model.location.LocationResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val MAX_LOCATION_FETCH_ATTEMPTS = 3
private const val LOCATION_RETRY_DELAY_MILLIS = 5_000L

class OfflineFirstLocationsRepository(
    private val locationResourceDao: LocationResourceDao,
    private val locationSource: PlatformLocationSource,
    private val ioDispatcher: CoroutineDispatcher,
) : LocationsRepository {

    override fun getLocationResources(): Flow<List<LocationResource>> {
        return locationResourceDao.getLocationResources()
            .map { it.map(LocationResourceEntity::asExternalModel) }
    }

    override fun getLocationResource(): Flow<LocationResource?> {
        return locationResourceDao.getLocationResources().map { locationResources ->
            locationResources.firstOrNull()?.asExternalModel()
        }
    }

    override suspend fun updateLocation(locationPermissionGranted: Boolean) {
        println(
            "OfflineFirstLocationsRepository: updateLocation(locationPermissionGranted=" +
                "$locationPermissionGranted), isPlatformLocationAvailable=${isPlatformLocationAvailable()}",
        )
        if (locationPermissionGranted && isPlatformLocationAvailable()) {
            fetchAndStoreLocationWithRetry()
        }
    }

    /**
     * A single [PlatformLocationSource.getCurrentLocation] attempt can come back empty even with
     * permission granted — a cold GPS fix genuinely takes a few seconds. Retries a few times with
     * a short delay instead of leaving the UI stuck on "no location" (and its manual retry button)
     * after one unlucky attempt.
     */
    private suspend fun fetchAndStoreLocationWithRetry() {
        for (attempt in 1..MAX_LOCATION_FETCH_ATTEMPTS) {
            val location = withContext(ioDispatcher) { locationSource.getCurrentLocation() }
            println("OfflineFirstLocationsRepository: fetch attempt $attempt/$MAX_LOCATION_FETCH_ATTEMPTS -> $location")
            if (location != null) {
                locationResourceDao.upsertLocationResources(
                    LocationResourceEntity(
                        id = 1,
                        time = location.time,
                        latitude = location.latitude,
                        longitude = location.longitude,
                    )
                )
                return
            }
            if (attempt < MAX_LOCATION_FETCH_ATTEMPTS) {
                delay(LOCATION_RETRY_DELAY_MILLIS)
            }
        }
    }
}
