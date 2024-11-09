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

package com.ngapp.metanmobile.core.data.repository.faq

import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.model.faq.asEntity
import com.ngapp.metanmobile.core.data.updateDataSync
import com.ngapp.metanmobile.core.database.dao.faq.FaqResourceDao
import com.ngapp.metanmobile.core.database.model.faq.FaqResourceEntity
import com.ngapp.metanmobile.core.database.model.faq.asExternalModel
import com.ngapp.metanmobile.core.network.MetanMobileParserDataSource
import com.ngapp.metanmobile.core.network.model.faq.NetworkFaqResource
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.toSet

class OfflineFirstFaqRepository @Inject constructor(
    private val parser: MetanMobileParserDataSource,
    private val faqResourceDao: FaqResourceDao,
) : FaqRepository {

    override fun getFaqList(query: FaqResourceQuery) =
        faqResourceDao.getFaqResources(filterFaqListPinned = query.filterFaqListPinned)
            .map { it.map(FaqResourceEntity::asExternalModel) }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        return synchronizer.updateDataSync(
            dataFetcher = { parser.getFaqList() },
            dataWriter = { networkFaqList ->
                val newData = networkFaqList.map(NetworkFaqResource::asEntity)
                val newIds = newData.map { it.id }.toSet()
                val existingIds = faqResourceDao.getAllFaqIds().toSet()
                val idsToDelete = existingIds - newIds
                faqResourceDao.deleteFaqResources(idsToDelete)
                faqResourceDao.upsertFaqResources(newData)
            }
        )
    }
}