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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

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

    override fun getLocationResource(): Flow<LocationResource> {
        return locationResourceDao.getLocationResources().map { locationResources ->
            if (locationResources.isNotEmpty()) {
                locationResources.first().asExternalModel()
            } else {
                LocationResource.init()
            }
        }
    }

    override suspend fun updateLocation(locationPermissionGranted: Boolean) {
        if (locationPermissionGranted) {
            runCatching {
                googleServicesChecker.isGoogleServicesAvailable
            }.onSuccess { isAvailable ->
                if (isAvailable) {
                    val location = getLocationData()?.asEntity()
                    location?.let {
//                        locationResourceDao.deleteLocationResources()
                        locationResourceDao.insertOrIgnoreLocationResource(it)
                    }
                } else {
                    // Do something if Google Services not available
                }
            }.onFailure { exception ->
                Log.e("updateLocation", exception.message.toString())
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLocationData(): Location? = withContext(ioDispatcher) {
        try {
            return@withContext Tasks.await(locationClient.lastLocation)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return@withContext null
    }
}

