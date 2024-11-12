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

package com.ngapp.metanmobile.core.data.repository.githubuser

import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.model.githubuser.asEntity
import com.ngapp.metanmobile.core.data.updateSingleDataSync
import com.ngapp.metanmobile.core.database.dao.githubuser.GithubUserResourceDao
import com.ngapp.metanmobile.core.database.model.githubuser.asExternalModel
import com.ngapp.metanmobile.core.network.GithubNetworkDataSource
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstGithubUserRepository @Inject constructor(
    val network: GithubNetworkDataSource,
    private val githubUserResourceDao: GithubUserResourceDao,
) : GithubUserRepository {

    override fun getGithubUser() =
        githubUserResourceDao.getGithubUserResource().map { it?.asExternalModel() }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        return synchronizer.updateSingleDataSync(
            dataFetcher = { network.getGithubUser() },
            dataWriter = {
                val newData = it.asEntity()
                githubUserResourceDao.upsertGithubUserResources(listOf(newData))
            }
        )
    }
}