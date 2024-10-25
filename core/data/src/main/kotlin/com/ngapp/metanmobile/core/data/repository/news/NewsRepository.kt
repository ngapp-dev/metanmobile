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

package com.ngapp.metanmobile.core.data.repository.news

import com.ngapp.metanmobile.core.data.Syncable
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.model.userdata.NewsSortingType
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import kotlinx.coroutines.flow.Flow

/**
 * Encapsulation class for query parameters for [NewsResource].
 *
 * This class holds various parameters used to filter and sort news resources
 * when querying from the repository.
 */
data class NewsResourceQuery(
    /**
     * A set of news IDs to filter the results by. If null, any news ID will match.
     * Useful for fetching specific news items.
     */
    val filterNewsIds: Set<String>? = null,

    /**
     * A boolean flag indicating whether to filter for pinned news items.
     * If true, only pinned news will be included in the results.
     * If false, all news items, regardless of their pinned status, will match.
     */
    val filterNewsPinned: Boolean = false,

    /**
     * The title of the station to filter news items by. If null, any news item will match.
     * This is useful for retrieving news relevant to a specific station.
     */
    val filterNewsByStationTitle: String? = null,

    /**
     * The sorting type to apply when fetching news resources.
     * It determines the primary criterion for sorting the results.
     * Default is [NewsSortingType.DATE], which sorts news by creation date.
     */
    val sortingType: NewsSortingType = NewsSortingType.DATE,

    /**
     * The sorting order to apply to the results.
     * It determines the direction of sorting (ascending or descending).
     * Default is [SortingOrder.DESC], which sorts the results in descending order.
     */
    val sortingOrder: SortingOrder = SortingOrder.DESC,

    /**
     * A search query to filter news resources by name or other relevant fields.
     * An empty string means no search filter will be applied.
     */
    val searchQuery: String = "",
)

/**
 * Data layer implementation for [NewsResource]
 */
interface NewsRepository : Syncable {
    fun getNewsResourcesAsc(query: NewsResourceQuery = NewsResourceQuery()): Flow<List<NewsResource>>
    fun getNewsResourcesDesc(query: NewsResourceQuery = NewsResourceQuery()): Flow<List<NewsResource>>
    fun getNewsResource(newsId: String): Flow<NewsResource>
}