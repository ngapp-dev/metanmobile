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

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks
import com.ngapp.metanmobile.core.common.network.Dispatcher
import com.ngapp.metanmobile.core.common.network.MMDispatchers.IO
import com.ngapp.metanmobile.core.data.model.location.asEntity
import com.ngapp.metanmobile.core.data.util.GoogleServicesChecker
import com.ngapp.metanmobile.core.database.dao.location.LocationResourceDao
import com.ngapp.metanmobile.core.database.model.location.LocationResourceEntity
import com.ngapp.metanmobile.core.database.model.location.asExternalModel
import com.ngapp.metanmobile.core.model.location.LocationResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val MAX_LOCATION_FETCH_ATTEMPTS = 3
private const val LOCATION_RETRY_DELAY_MILLIS = 5_000L

class OfflineFirstLocationsRepository @Inject constructor(
    private val locationResourceDao: LocationResourceDao,
    private val locationClient: FusedLocationProviderClient,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val googleServicesChecker: GoogleServicesChecker,
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
        if (locationPermissionGranted) {
            runCatching {
                googleServicesChecker.isGoogleServicesAvailable
            }.onSuccess { isAvailable ->
                if (isAvailable) {
                    fetchAndStoreLocationWithRetry()
                } else {
                    // Do something if Google Services not available
                }
            }.onFailure { exception ->
                Log.e("updateLocation", exception.message.toString())
            }
        }
    }

    /**
     * A single [getLocationData] attempt can come back empty even with permission granted — a
     * cold GPS fix genuinely takes a few seconds. Retries a few times with a short delay instead
     * of leaving the UI stuck on "no location" (and its manual retry button) after one unlucky
     * attempt.
     */
    private suspend fun fetchAndStoreLocationWithRetry() {
        for (attempt in 1..MAX_LOCATION_FETCH_ATTEMPTS) {
            val location = getLocationData()?.asEntity()
            if (location != null) {
                locationResourceDao.upsertLocationResources(location)
                return
            }
            if (attempt < MAX_LOCATION_FETCH_ATTEMPTS) {
                delay(LOCATION_RETRY_DELAY_MILLIS)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLocationData(): Location? = withContext(ioDispatcher) {
        val cached = runCatching { Tasks.await(locationClient.lastLocation) }.getOrNull()
        // lastLocation is just a cache — null whenever the device has never computed a fix
        // (fresh device, GPS/network location off). Fall back to one active request instead of
        // silently giving up, so "permission granted" doesn't mean "stuck with no location
        // forever" until something else on the device happens to trigger a fix.
        cached ?: runCatching {
            Tasks.await(
                locationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token,
                )
            )
        }.getOrNull()
    }
}

