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

package com.ngapp.metanmobile.core.database.dao.career

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ngapp.metanmobile.core.database.model.career.CareerResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [CareerResourceEntity] access
 */
@Dao
interface CareerResourceDao {

    /**
     * Fetches career resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM career_resources
            WHERE 
                CASE WHEN :useFilterCareerIds
                    THEN id IN (:filterCareerIds)
                    ELSE 1
                END
            ORDER BY id DESC
    """,
    )
    fun getCareerResources(
        useFilterCareerIds: Boolean = false,
        filterCareerIds: Set<String> = emptySet(),
    ): Flow<List<CareerResourceEntity>>

    @Query(value = """SELECT * FROM career_resources WHERE id = :id""")
    fun getCareerResource(id: String): Flow<CareerResourceEntity>

    /**
     * Inserts [entities] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCareerResources(entities: List<CareerResourceEntity>): List<Long>

    /**
     * Inserts or updates [careerResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertCareerResources(careerResourceEntities: List<CareerResourceEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(value = """DELETE FROM career_resources WHERE id in (:ids)""")
    suspend fun deleteCareerResources(ids: List<String>)
}
