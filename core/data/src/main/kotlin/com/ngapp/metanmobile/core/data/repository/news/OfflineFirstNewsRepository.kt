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

import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.model.news.asEntity
import com.ngapp.metanmobile.core.data.updateDataSync
import com.ngapp.metanmobile.core.database.dao.news.NewsResourceDao
import com.ngapp.metanmobile.core.database.model.news.NewsResourceEntity
import com.ngapp.metanmobile.core.database.model.news.asExternalModel
import com.ngapp.metanmobile.core.network.MetanMobileParserDataSource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class OfflineFirstNewsRepository @Inject constructor(
    private val parser: MetanMobileParserDataSource,
    private val newsResourceDao: NewsResourceDao,
) : NewsRepository {

    override fun getNewsResourcesAsc(query: NewsResourceQuery) =
        newsResourceDao.getNewsResourcesAsc(
            useFilterNewsIds = query.filterNewsIds != null,
            useFilterNewsByStationTitle = query.filterNewsByStationTitle != null,
            filterNewsIds = query.filterNewsIds ?: emptySet(),
            filterNewsPinned = query.filterNewsPinned,
            filterNewsByStationTitle = query.filterNewsByStationTitle ?: "",
            sortingType = query.sortingType.name,
            searchQuery = query.searchQuery,
        ).map { it.map(NewsResourceEntity::asExternalModel) }

    override fun getNewsResourcesDesc(query: NewsResourceQuery) =
        newsResourceDao.getNewsResourcesDesc(
            useFilterNewsIds = query.filterNewsIds != null,
            useFilterNewsByStationTitle = query.filterNewsByStationTitle != null,
            filterNewsIds = query.filterNewsIds ?: emptySet(),
            filterNewsPinned = query.filterNewsPinned,
            filterNewsByStationTitle = query.filterNewsByStationTitle ?: "",
            sortingType = query.sortingType.name,
            searchQuery = query.searchQuery,
        ).map { it.map(NewsResourceEntity::asExternalModel) }

    override fun getNewsResource(newsId: String) =
        newsResourceDao.getNewsResource(newsId).map(NewsResourceEntity::asExternalModel)

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        return synchronizer.updateDataSync(
            dataFetcher = { parser.getNewsList() },
            dataWriter = { networkNewsList ->
                val newData = networkNewsList.map(NetworkNewsResource::asEntity)
                val newIds = newData.map { it.id }.toSet()
                val existingIds = newsResourceDao.getAllNewsIds().toSet()
                val idsToDelete = existingIds - newIds
                newsResourceDao.deleteNewsResources(idsToDelete)
                newsResourceDao.upsertNewsResources(newData)
            }
        )
    }
}