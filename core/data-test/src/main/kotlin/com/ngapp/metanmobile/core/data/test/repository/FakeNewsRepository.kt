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

package com.ngapp.metanmobile.core.data.test.repository

import com.ngapp.metanmobile.core.common.network.Dispatcher
import com.ngapp.metanmobile.core.common.network.MMDispatchers.IO
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.model.news.asEntity
import com.ngapp.metanmobile.core.data.repository.news.NewsRepository
import com.ngapp.metanmobile.core.data.repository.news.NewsResourceQuery
import com.ngapp.metanmobile.core.database.model.news.NewsResourceEntity
import com.ngapp.metanmobile.core.database.model.news.asExternalModel
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.model.userdata.NewsSortingType
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.network.demo.DemoMetanMobileParser
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import com.ngapp.metanmobile.core.network.network.MetanMobileParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Fake implementation of the [NewsRepository] that retrieves the news resources from a JSON String.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
internal class FakeNewsRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val parser: MetanMobileParser,
) : NewsRepository {

    override fun getNewsResourcesAsc(query: NewsResourceQuery): Flow<List<NewsResource>> = flow {
        val newsList = parser.getNewsList(true)
            .map(NetworkNewsResource::asEntity)
            .map(NewsResourceEntity::asExternalModel)
            .applyQueryFilters(query)
            .applySorting(query.copy(sortingOrder = SortingOrder.ASC))
        emit(newsList)
    }.flowOn(ioDispatcher)

    override fun getNewsResourcesDesc(query: NewsResourceQuery): Flow<List<NewsResource>> = flow {
        val newsList = parser.getNewsList(true)
            .map(NetworkNewsResource::asEntity)
            .map(NewsResourceEntity::asExternalModel)
            .applyQueryFilters(query)
            .applySorting(query.copy(sortingOrder = SortingOrder.DESC))
        emit(newsList)
    }.flowOn(ioDispatcher)

    override fun getNewsResource(newsId: String): Flow<NewsResource> = flow {
        val newsResource = parser.getNews(newsId, true)
            ?.asEntity()
            ?.asExternalModel()
        emit(newsResource ?: NewsResource.init())
    }.flowOn(ioDispatcher)

    override suspend fun syncWith(synchronizer: Synchronizer) = true

    private fun List<NewsResource>.applyQueryFilters(query: NewsResourceQuery): List<NewsResource> {
        return this
            .filter { news ->
                (query.filterNewsIds?.contains(news.id) ?: true) &&
                        (!query.filterNewsPinned || news.isPinned == 1) &&
                        (query.filterNewsByStationTitle?.let { it == news.relatedStation }
                            ?: true) &&
                        (news.title.contains(query.searchQuery, ignoreCase = true) ||
                                news.content.contains(query.searchQuery, ignoreCase = true))
            }
    }

    private fun List<NewsResource>.applySorting(query: NewsResourceQuery): List<NewsResource> {
        return when (query.sortingType) {
            NewsSortingType.DATE -> {
                if (query.sortingOrder == SortingOrder.ASC) {
                    this.sortedBy { it.dateCreated }
                } else {
                    this.sortedByDescending { it.dateCreated }
                }
            }

            NewsSortingType.NAME -> {
                if (query.sortingOrder == SortingOrder.ASC) {
                    this.sortedBy { it.title }
                } else {
                    this.sortedByDescending { it.title }
                }
            }

            else -> this
        }
    }
}
