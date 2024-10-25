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

package com.ngapp.metanmobile.core.database.dao.faq

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ngapp.metanmobile.core.database.model.faq.FaqResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [FaqResourceEntity] access
 */
@Dao
interface FaqResourceDao {

    /**
     * Fetches faq resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM faq_resources
            WHERE 
                (CASE WHEN :useFilterFaqIds THEN id IN (:filterFaqIds) ELSE 1=1 END)
            AND (CASE WHEN :filterFaqListPinned THEN is_pinned = 1 ELSE 1=1 END)
            ORDER BY id DESC
    """,
    )
    fun getFaqResources(
        useFilterFaqIds: Boolean = false,
        filterFaqIds: Set<String> = emptySet(),
        filterFaqListPinned: Boolean = false
    ): Flow<List<FaqResourceEntity>>

    @Query(value = """SELECT * FROM faq_resources WHERE id = :id""")
    fun getFaqResource(id: String): Flow<FaqResourceEntity>

    @Query("SELECT id FROM faq_resources")
    suspend fun getAllFaqIds(): List<String>
    /**
     * Inserts [entities] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreFaqResources(entities: List<FaqResourceEntity>): List<Long>

    /**
     * Inserts or updates [faqResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertFaqResources(faqResourceEntities: List<FaqResourceEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(value = """DELETE FROM faq_resources WHERE id in (:ids)""")
    suspend fun deleteFaqResources(ids: Set<String>)
}
