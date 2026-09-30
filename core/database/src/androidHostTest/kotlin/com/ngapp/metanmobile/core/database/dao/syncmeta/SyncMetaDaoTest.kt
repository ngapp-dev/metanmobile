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

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ngapp.metanmobile.core.database.MetanMobileDatabase
import com.ngapp.metanmobile.core.database.model.syncmeta.SyncMetaEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SyncMetaDaoTest {

    private lateinit var database: MetanMobileDatabase
    private lateinit var dao: SyncMetaDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MetanMobileDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.syncMetaDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `a never-synced database has no version`() = runTest {
        assertNull(dao.getSyncVersion())
    }

    @Test
    fun `upsert keeps a single row with the latest version`() = runTest {
        dao.upsertSyncMeta(SyncMetaEntity(syncVersion = 5))
        dao.upsertSyncMeta(SyncMetaEntity(syncVersion = 7))

        assertEquals(7L, dao.getSyncVersion())
    }

    @Test
    fun `clearing the database clears the version`() = runTest {
        dao.upsertSyncMeta(SyncMetaEntity(syncVersion = 5))

        database.clearAllTables()

        assertNull(dao.getSyncVersion())
    }
}
