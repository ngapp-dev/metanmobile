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

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ngapp.metanmobile.core.database.MetanMobileDatabase
import com.ngapp.metanmobile.core.database.model.station.StationResourceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

/**
 * Runs [StationResourceDao]'s actual SQL against a real (in-memory) Room database - unlike the
 * repository-level tests in `core:data-test`, which mock the DAO away and so can't catch a bug
 * that lives in the query itself, such as `getStationResourcesAsc`/`getStationResourcesDesc`
 * sorting in the wrong direction relative to their own names.
 *
 * Pinned to sdk = 34: the installed Robolectric version doesn't yet support the project's
 * targetSdk 36, this is a test-only simulated platform choice, unrelated to the app's real
 * min/target/compile SDK.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StationResourceDaoTest {

    private lateinit var database: MetanMobileDatabase
    private lateinit var dao: StationResourceDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MetanMobileDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.stationResourceDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `getStationResourcesAsc sorts by title ascending`() = runTest {
        dao.upsertStationResources(listOf(station(code = "b", title = "Beta"), station(code = "a", title = "Alpha")))

        val result = dao.getStationResourcesAsc(sortingType = "STATION_NAME", searchQuery = "").first()

        assertEquals(listOf("Alpha", "Beta"), result.map { it.title })
    }

    @Test
    fun `getStationResourcesDesc sorts by title descending`() = runTest {
        dao.upsertStationResources(listOf(station(code = "a", title = "Alpha"), station(code = "b", title = "Beta")))

        val result = dao.getStationResourcesDesc(sortingType = "STATION_NAME", searchQuery = "").first()

        assertEquals(listOf("Beta", "Alpha"), result.map { it.title })
    }

    private fun station(code: String, title: String) = StationResourceEntity(
        id = code,
        code = code,
        previewPicture = "",
        detailPicture = "",
        isActive = 1,
        isOperate = 1,
        type = "",
        address = "",
        region = "",
        phones = "",
        service = "",
        workingTime = "",
        payment = "",
        latitude = "",
        longitude = "",
        busyOnMonday = "",
        busyOnTuesday = "",
        busyOnWednesday = "",
        busyOnThursday = "",
        busyOnFriday = "",
        busyOnSaturday = "",
        busyOnSunday = "",
        googleTag = "",
        googleMapsTag = "",
        yandexTag = "",
        title = title,
        dateCreated = 0L,
        url = "",
    )
}
