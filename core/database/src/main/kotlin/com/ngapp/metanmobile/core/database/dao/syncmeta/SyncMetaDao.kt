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

package com.ngapp.metanmobile.core.database.dao.syncmeta

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ngapp.metanmobile.core.database.model.syncmeta.SyncMetaEntity

/**
 * DAO for [SyncMetaEntity] access
 */
@Dao
interface SyncMetaDao {

    /**
     * Returns the stored sync version, or null if this database has never been synced
     */
    @Query(value = """SELECT sync_version FROM sync_meta WHERE id = 0""")
    suspend fun getSyncVersion(): Long?

    @Upsert
    suspend fun upsertSyncMeta(entity: SyncMetaEntity)
}
