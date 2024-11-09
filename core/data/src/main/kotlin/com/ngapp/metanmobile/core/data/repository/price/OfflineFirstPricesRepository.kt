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

package com.ngapp.metanmobile.core.data.repository.price

import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.model.price.asEntity
import com.ngapp.metanmobile.core.data.updateDataSync
import com.ngapp.metanmobile.core.database.dao.price.PriceResourceDao
import com.ngapp.metanmobile.core.database.model.price.PriceResourceEntity
import com.ngapp.metanmobile.core.database.model.price.asExternalModel
import com.ngapp.metanmobile.core.network.MetanMobileParserDataSource
import com.ngapp.metanmobile.core.network.model.price.NetworkPriceResource
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstPricesRepository @Inject constructor(
    private val parser: MetanMobileParserDataSource,
    private val priceResourceDao: PriceResourceDao,
) : PricesRepository {

    override fun getFuelPrice() = priceResourceDao.getPriceResource().map { it?.asExternalModel() }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        return synchronizer.updateDataSync(
            dataFetcher = { parser.getFuelPrices() },
            dataWriter = {
                val newData = it.map(NetworkPriceResource::asEntity)
                priceResourceDao.upsertPriceResources(newData)
            }
        )
    }
}