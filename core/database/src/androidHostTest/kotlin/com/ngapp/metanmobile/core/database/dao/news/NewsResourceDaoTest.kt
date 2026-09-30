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

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ngapp.metanmobile.core.database.MetanMobileDatabase
import com.ngapp.metanmobile.core.database.model.news.NewsResourceEntity
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
 * Covers the `is_in_feed` flag: news the server tombstoned must drop out of every list, yet stay
 * reachable by id so a deep link to it still opens the detail screen.
 *
 * Pinned to sdk = 34 for the same reason as [com.ngapp.metanmobile.core.database.dao.station.StationResourceDaoTest].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NewsResourceDaoTest {

    private lateinit var database: MetanMobileDatabase
    private lateinit var dao: NewsResourceDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MetanMobileDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.newsResourceDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `new rows are in the feed by default`() = runTest {
        dao.upsertNewsResources(listOf(news("a"), news("b")))

        assertEquals(setOf("a", "b"), listIds())
    }

    @Test
    fun `marked rows leave both list queries`() = runTest {
        dao.upsertNewsResources(listOf(news("a"), news("b"), news("c")))

        dao.markNewsResourcesNotInFeed(setOf("b"))

        assertEquals(setOf("a", "c"), listIds())
        assertEquals(
            setOf("a", "c"),
            dao.getNewsResourcesAsc(sortingType = "DATE", searchQuery = "").first().map { it.id }.toSet(),
        )
    }

    @Test
    fun `marked rows are still found by id`() = runTest {
        dao.upsertNewsResources(listOf(news("a")))

        dao.markNewsResourcesNotInFeed(setOf("a"))

        val detail = dao.getNewsResource("a").first()
        assertEquals("a", detail.id)
        assertEquals(0, detail.isInFeed)
    }

    @Test
    fun `marked rows stay out of filtered and searched lists`() = runTest {
        dao.upsertNewsResources(listOf(news("a", title = "Pinned", isPinned = 1), news("b", title = "Pinned", isPinned = 1)))
        dao.markNewsResourcesNotInFeed(setOf("b"))

        val pinned = dao.getNewsResourcesDesc(filterNewsPinned = true, sortingType = "DATE", searchQuery = "").first()
        val searched = dao.getNewsResourcesDesc(sortingType = "DATE", searchQuery = "Pinned").first()
        val byId = dao.getNewsResourcesDesc(
            useFilterNewsIds = true,
            filterNewsIds = setOf("a", "b"),
            sortingType = "DATE",
            searchQuery = "",
        ).first()

        assertEquals(listOf("a"), pinned.map { it.id })
        assertEquals(listOf("a"), searched.map { it.id })
        assertEquals(listOf("a"), byId.map { it.id })
    }

    @Test
    fun `upserting a marked row brings it back into the feed`() = runTest {
        dao.upsertNewsResources(listOf(news("a")))
        dao.markNewsResourcesNotInFeed(setOf("a"))

        dao.upsertNewsResources(listOf(news("a")))

        assertEquals(setOf("a"), listIds())
    }

    @Test
    fun `marking unknown ids is a no-op`() = runTest {
        dao.upsertNewsResources(listOf(news("a")))

        dao.markNewsResourcesNotInFeed(setOf("missing"))
        dao.markNewsResourcesNotInFeed(emptySet())

        assertEquals(setOf("a"), listIds())
    }

    private suspend fun listIds(): Set<String> =
        dao.getNewsResourcesDesc(sortingType = "DATE", searchQuery = "").first().map { it.id }.toSet()

    private fun news(id: String, title: String = id, isPinned: Int = 0) = NewsResourceEntity(
        id = id,
        code = id,
        isPinned = isPinned,
        previewPicture = "",
        detailPicture = "",
        isActive = 1,
        isOperate = 1,
        relatedStation = "",
        title = title,
        dateCreated = 0L,
        description = "",
        content = "",
        url = "",
        isSearchable = 1,
    )
}
