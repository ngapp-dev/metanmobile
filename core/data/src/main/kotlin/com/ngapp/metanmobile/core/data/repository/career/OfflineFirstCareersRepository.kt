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

package com.ngapp.metanmobile.core.data.repository.career

import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.model.career.asEntity
import com.ngapp.metanmobile.core.data.updateDataSync
import com.ngapp.metanmobile.core.database.dao.career.CareerResourceDao
import com.ngapp.metanmobile.core.database.model.career.CareerResourceEntity
import com.ngapp.metanmobile.core.database.model.career.asExternalModel
import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.network.MetanMobileParserDataSource
import com.ngapp.metanmobile.core.network.model.career.NetworkCareerResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstCareersRepository @Inject constructor(
    private val parser: MetanMobileParserDataSource,
    private val careerResourceDao: CareerResourceDao,
) : CareersRepository {

    override fun getCareerList(): Flow<List<CareerResource>> =
        careerResourceDao.getCareerResources().map { it.map(CareerResourceEntity::asExternalModel) }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        return synchronizer.updateDataSync(
            dataFetcher = { parser.getCareerList() },
            dataWriter = {
                val newData = it.map(NetworkCareerResource::asEntity)
                careerResourceDao.upsertCareerResources(newData)
            }
        )
    }
}