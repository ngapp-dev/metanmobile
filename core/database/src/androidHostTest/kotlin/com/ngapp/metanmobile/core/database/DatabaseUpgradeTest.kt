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

package com.ngapp.metanmobile.core.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import com.ngapp.metanmobile.core.database.model.news.NewsResourceEntity
import com.ngapp.metanmobile.core.database.model.syncmeta.SyncMetaEntity
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * What every released app (2.1.5 – 2.3.1, all on Room v8 with the same file name) goes through
 * on update: a v8 database file already on disk, opened by the production [databaseInstance]
 * builder at [METAN_MOBILE_DATABASE_VERSION]. The v8 file is built from the exported 8.json, so it
 * matches what those releases created byte for byte in schema, including Room's identity hash.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DatabaseUpgradeTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private var database: MetanMobileDatabase? = null

    @Before
    fun setUp() {
        startKoin { modules(module { single<Context> { context } }) }
    }

    @After
    fun tearDown() {
        database?.close()
        stopKoin()
        context.deleteDatabase(METAN_MOBILE_DATABASE_NAME)
    }

    @Test
    fun `a v8 database from a released app opens without crashing`() = runTest {
        createV8Database()

        database = databaseInstance()

        // Any query forces Room to open (and migrate) the file.
        assertEquals(emptyList(), database!!.stationResourceDao().getAllStationIds())
        assertEquals(METAN_MOBILE_DATABASE_VERSION, readUserVersion())
    }

    @Test
    fun `the v8 cache is dropped and the sync version starts empty`() = runTest {
        createV8Database()

        database = databaseInstance()

        assertEquals(emptyList(), database!!.stationResourceDao().getAllStationIds())
        assertEquals(emptyList(), database!!.newsResourceDao().getAllNewsIds())
        // No version -> DataSyncCoordinator asks for since=0, a full re-download.
        assertNull(database!!.syncMetaDao().getSyncVersion())
    }

    @Test
    fun `the recreated database has the v9 schema`() = runTest {
        createV8Database()

        database = databaseInstance().also { db ->
            db.newsResourceDao().upsertNewsResources(listOf(news("a")))
            db.newsResourceDao().markNewsResourcesNotInFeed(setOf("a"))
            db.syncMetaDao().upsertSyncMeta(SyncMetaEntity(syncVersion = 3))
        }

        assertEquals(0, database!!.newsResourceDao().getNewsResource("a").first().isInFeed)
        assertEquals(3L, database!!.syncMetaDao().getSyncVersion())
    }

    @Test
    fun `a fresh install creates the database from scratch`() = runTest {
        assertTrue(!context.getDatabasePath(METAN_MOBILE_DATABASE_NAME).exists())

        database = databaseInstance()

        assertNull(database!!.syncMetaDao().getSyncVersion())
        assertEquals(METAN_MOBILE_DATABASE_VERSION, readUserVersion())
    }

    /** Writes a v8 file exactly as Room v8 would have, with one cached station and news row in it. */
    private fun createV8Database() {
        val schema = Json.parseToJsonElement(
            File("schemas/com.ngapp.metanmobile.core.database.MetanMobileDatabase/8.json").readText(),
        ).jsonObject.getValue("database").jsonObject
        assertEquals(8, schema.getValue("version").jsonPrimitive.content.toInt())

        val file = context.getDatabasePath(METAN_MOBILE_DATABASE_NAME)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { db ->
            schema.getValue("entities").jsonArray.forEach { entity ->
                val table = entity.jsonObject.getValue("tableName").jsonPrimitive.content
                db.execSQL(entity.jsonObject.getValue("createSql").jsonPrimitive.content.replace("\${TABLE_NAME}", table))
            }
            schema.getValue("setupQueries").jsonArray.forEach { db.execSQL(it.jsonPrimitive.content) }

            db.execSQL(
                "INSERT INTO station_resources VALUES ('s1','s1','','',1,1,'','','','','','','','','','','','','','','','','','','','Old station',0,'')",
            )
            db.execSQL("INSERT INTO news_resources VALUES ('n1','n1',0,'','',1,1,'','Old news',0,'','','',1)")
            db.version = 8
        }
    }

    private fun readUserVersion(): Int =
        SQLiteDatabase.openDatabase(
            context.getDatabasePath(METAN_MOBILE_DATABASE_NAME).path,
            null,
            SQLiteDatabase.OPEN_READONLY,
        ).use { it.version }

    private fun news(id: String) = NewsResourceEntity(
        id = id,
        code = id,
        isPinned = 0,
        previewPicture = "",
        detailPicture = "",
        isActive = 1,
        isOperate = 1,
        relatedStation = "",
        title = id,
        dateCreated = 0L,
        description = "",
        content = "",
        url = "",
        isSearchable = 1,
    )
}
