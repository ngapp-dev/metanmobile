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

package com.ngapp.metanmobile.core.database.dao.contact

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ngapp.metanmobile.core.database.model.contact.ContactResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [ContactResourceEntity] access
 */
@Dao
interface ContactResourceDao {

    @Query(value = """SELECT * FROM contact_resources LIMIT 1""")
    fun getContactResource(): Flow<ContactResourceEntity?>

    /**
     * Inserts or updates [contactResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertContactResources(contactResourceEntities: List<ContactResourceEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(value = """DELETE FROM contact_resources WHERE id in (:ids)""")
    suspend fun deleteContactResources(ids: List<String>)
}
