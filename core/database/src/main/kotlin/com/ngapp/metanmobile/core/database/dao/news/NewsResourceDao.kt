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

package com.ngapp.metanmobile.core.database.dao.news

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ngapp.metanmobile.core.database.model.news.NewsResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [NewsResourceEntity] access
 */
@Dao
interface NewsResourceDao {

    /**
     * Fetches news resources that match the query parameters with ascending order
     */
    @Transaction
    @Query(
        value = """
        SELECT * FROM news_resources
        WHERE 
            (CASE WHEN :useFilterNewsIds
                THEN id IN (:filterNewsIds)
                ELSE 1=1
            END)
            AND (CASE WHEN :filterNewsPinned 
                THEN is_pinned = 1
                ELSE 1=1
            END)
            AND (CASE WHEN :useFilterNewsByStationTitle
                THEN related_station = :filterNewsByStationTitle
                ELSE 1=1
            END)
            AND
            (CASE WHEN :searchQuery != '' 
                THEN title LIKE '%' || :searchQuery || '%' 
                ELSE 1=1
            END)
        ORDER BY 
            CASE 
                WHEN :sortingType = 'DATE' THEN date_created 
                WHEN :sortingType = 'NAME' THEN title 
                ELSE NULL 
            END
            ASC
    """
    )
    fun getNewsResourcesAsc(
        useFilterNewsIds: Boolean = false,
        useFilterNewsByStationTitle: Boolean = false,
        filterNewsIds: Set<String> = emptySet(),
        filterNewsPinned: Boolean = false,
        filterNewsByStationTitle: String = "",
        sortingType: String,
        searchQuery: String,
    ): Flow<List<NewsResourceEntity>>

    /**
     * Fetches news resources that match the query parameters with descending order
     */
    @Transaction
    @Query(
        value = """
        SELECT * FROM news_resources
        WHERE 
            (CASE WHEN :useFilterNewsIds
                THEN id IN (:filterNewsIds)
                ELSE 1=1
            END)
            AND (CASE WHEN :filterNewsPinned 
                THEN is_pinned = 1
                ELSE 1=1
            END)
            AND (CASE WHEN :useFilterNewsByStationTitle
                THEN related_station = :filterNewsByStationTitle
                ELSE 1=1
            END)
            AND
            (CASE WHEN :searchQuery != '' 
                THEN title LIKE '%' || :searchQuery || '%' 
                ELSE 1=1
            END)
        ORDER BY 
            CASE 
                WHEN :sortingType = 'DATE' THEN date_created 
                WHEN :sortingType = 'NAME' THEN title 
                ELSE NULL 
            END
            DESC
    """
    )
    fun getNewsResourcesDesc(
        useFilterNewsIds: Boolean = false,
        useFilterNewsByStationTitle: Boolean = false,
        filterNewsIds: Set<String> = emptySet(),
        filterNewsPinned: Boolean = false,
        filterNewsByStationTitle: String = "",
        sortingType: String,
        searchQuery: String,
    ): Flow<List<NewsResourceEntity>>

    @Query(value = """SELECT * FROM news_resources WHERE id = :id""")
    fun getNewsResource(id: String): Flow<NewsResourceEntity>

    @Query("SELECT id FROM news_resources")
    suspend fun getAllNewsIds(): List<String>

    /**
     * Inserts [entities] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreNewsResources(entities: List<NewsResourceEntity>): List<Long>

    /**
     * Inserts or updates [newsResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertNewsResources(newsResourceEntities: List<NewsResourceEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(value = """DELETE FROM news_resources WHERE id in (:ids)""")
    suspend fun deleteNewsResources(ids: Set<String>)
}
