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

package com.ngapp.metanmobile.core.database.dao.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.ngapp.metanmobile.core.database.model.location.LocationResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationResourceDao {

    /**
     * Inserts or updates [locationResourceEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertLocationResources(locationResourceEntity: LocationResourceEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreLocationResource(location: LocationResourceEntity)

    @Query("DELETE FROM location_resources")
    suspend fun deleteLocationResources()

    @Query("SELECT * FROM location_resources ORDER BY time")
    fun getLocationResources(): Flow<List<LocationResourceEntity>>
}
