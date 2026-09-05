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

package com.ngapp.metanmobile.core.testing.repository

import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.news.NewsRepository
import com.ngapp.metanmobile.core.data.repository.news.NewsResourceQuery
import com.ngapp.metanmobile.core.model.news.NewsResource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class TestNewsRepository : NewsRepository {

    /**
     * The backing hot flow for the list of news ids for testing.
     *
     * Seeded with an empty list so collectors get a value right away, matching
     * `NewsResourceDao`'s Room-backed flow — see [TestLocationsRepository] for why an un-seeded
     * `MutableSharedFlow(replay = 1)` is a trap for anything that `combine`s this with another
     * flow.
     */
    private val newsResourcesFlow =
        MutableSharedFlow<List<NewsResource>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
            tryEmit(emptyList())
        }

    /**
     * A test-only API to allow controlling the list of news resources from tests.
     */
    fun sendNewsResources(newsResources: List<NewsResource>) {
        newsResourcesFlow.tryEmit(newsResources)
    }

    override fun getNewsResourcesAsc(query: NewsResourceQuery): Flow<List<NewsResource>> =
        newsResourcesFlow.map { newsResources -> applyQuery(newsResources, query) }

    override fun getNewsResourcesDesc(query: NewsResourceQuery): Flow<List<NewsResource>> =
        newsResourcesFlow.map { newsResources -> applyQuery(newsResources, query) }

    /**
     * Mirrors the `WHERE` clause of `NewsResourceDao.getNewsResourcesAsc/Desc`.
     */
    private fun applyQuery(
        newsResources: List<NewsResource>,
        query: NewsResourceQuery,
    ): List<NewsResource> {
        var result = newsResources
        query.filterNewsIds?.let { filterNewsIds ->
            result = result.filter { it.id in filterNewsIds }
        }
        // NOTE: filterNewsPinned = false means "no filter" (any pinned status matches), not
        // "unpinned only" — matching the KDoc on NewsResourceQuery.filterNewsPinned and the DAO's
        // `CASE WHEN :filterNewsPinned THEN is_pinned = 1 ELSE 1=1 END`.
        if (query.filterNewsPinned) {
            result = result.filter { it.isPinned == 1 }
        }
        query.filterNewsByStationTitle?.let { filterNewsByStationTitle ->
            result = result.filter { it.relatedStation == filterNewsByStationTitle }
        }
        if (query.searchQuery.isNotEmpty()) {
            result = result.filter { it.title.contains(query.searchQuery, ignoreCase = true) }
        }
        return result
    }

    /**
     * `mapNotNull` (not `.map { .first { ... } }`) so subscribing before [newsId] has been sent —
     * e.g. against the initial empty-list seed — simply emits nothing yet instead of throwing
     * `NoSuchElementException`, matching a Room query that has no matching row so far.
     */
    override fun getNewsResource(newsId: String): Flow<NewsResource> =
        newsResourcesFlow.mapNotNull { newsResources -> newsResources.find { it.id == newsId } }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
