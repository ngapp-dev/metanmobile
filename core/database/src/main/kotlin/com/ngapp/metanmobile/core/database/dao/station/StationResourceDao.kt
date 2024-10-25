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

package com.ngapp.metanmobile.core.database.dao.station

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ngapp.metanmobile.core.database.model.station.StationResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [StationResourceEntity] access
 */
@Dao
interface StationResourceDao {

    /**
     * Fetches station resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM station_resources
            WHERE 
                (CASE WHEN :useFilterStationCodes
                    THEN code IN (:filterStationCodes)
                    ELSE 1=1
                END)
                AND
                (CASE WHEN :useFilterStationTypes
                    THEN type IN (:filterStationTypes)
                    ELSE 1=1
                END)
                AND
                (CASE WHEN :searchQuery != '' 
                    THEN title LIKE '%' || :searchQuery || '%' 
                    ELSE 1=1
                END)
            ORDER BY 
            CASE
                WHEN :sortingType = 'DISTANCE' THEN distance_between 
                WHEN :sortingType = 'STATION_NAME' THEN title 
                ELSE NULL 
            END
            ASC
    """,
    )
    fun getStationResourcesDesc(
        useFilterStationCodes: Boolean = false,
        filterStationCodes: Set<String> = emptySet(),
        useFilterStationTypes: Boolean = false,
        filterStationTypes: Set<String> = emptySet(),
        sortingType: String,
        searchQuery: String,
    ): Flow<List<StationResourceEntity>>

    @Transaction
    @Query(
        value = """
            SELECT * FROM station_resources
            WHERE 
                (CASE WHEN :useFilterStationCodes
                    THEN code IN (:filterStationCodes)
                    ELSE 1=1
                END)
                AND
                (CASE WHEN :useFilterStationTypes
                    THEN type IN (:filterStationTypes)
                    ELSE 1=1
                END)
                AND
                (CASE WHEN :searchQuery != '' 
                    THEN title LIKE '%' || :searchQuery || '%' 
                    ELSE 1=1
                END)
            ORDER BY 
            CASE
                WHEN :sortingType = 'DISTANCE' THEN distance_between 
                WHEN :sortingType = 'STATION_NAME' THEN title 
                ELSE NULL 
            END
            DESC
    """,
    )
    fun getStationResourcesAsc(
        useFilterStationCodes: Boolean = false,
        filterStationCodes: Set<String> = emptySet(),
        useFilterStationTypes: Boolean = false,
        filterStationTypes: Set<String> = emptySet(),
        sortingType: String,
        searchQuery: String,
    ): Flow<List<StationResourceEntity>>

    @Query(value = """SELECT * FROM station_resources WHERE code = :code""")
    fun getStationResource(code: String): Flow<StationResourceEntity>

    @Query("SELECT code FROM station_resources")
    suspend fun getAllStationIds(): List<String>
    /**
     * Inserts [entities] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreStationResources(entities: List<StationResourceEntity>): List<Long>

    /**
     * Inserts or updates [stationResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertStationResources(stationResourceEntities: List<StationResourceEntity>)

    /**
     * Deletes rows in the db matching the specified [codes]
     */
    @Query(value = """DELETE FROM station_resources WHERE code in (:codes)""")
    suspend fun deleteStationResources(codes: Set<String>)
}
