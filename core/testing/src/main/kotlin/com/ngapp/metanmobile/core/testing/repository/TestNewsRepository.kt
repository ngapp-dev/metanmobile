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

class TestNewsRepository : NewsRepository {

    /**
     * The backing hot flow for the list of news ids for testing.
     */
    private val newsResourcesFlow: MutableSharedFlow<List<NewsResource>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     * A test-only API to allow controlling the list of news resources from tests.
     */
    fun sendNewsResources(newsResources: List<NewsResource>) {
        newsResourcesFlow.tryEmit(newsResources)
    }

    override fun getNewsResourcesAsc(query: NewsResourceQuery): Flow<List<NewsResource>> =
        newsResourcesFlow.map { newsResources ->
            var result = newsResources
            query.filterNewsIds?.let { filterNewsIds ->
                result = result.filter { it.id in filterNewsIds }
            }
            query.filterNewsPinned.let { filterNewsPinned ->
                result = result.filter { it.isPinned == 1 }
            }
            result
        }


    override fun getNewsResourcesDesc(query: NewsResourceQuery): Flow<List<NewsResource>> =
        newsResourcesFlow.map { newsResources ->
            var result = newsResources
            query.filterNewsIds?.let { filterNewsIds ->
                result = result.filter { it.id in filterNewsIds }
            }
            query.filterNewsPinned.let { filterNewsPinned ->
                result = result.filter { it.isPinned == 1 }
            }
            result
        }

    override fun getNewsResource(newsId: String): Flow<NewsResource> =
        newsResourcesFlow.map { newsResources ->
            newsResources.first { it.id == newsId }
        }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
