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
import com.ngapp.metanmobile.core.data.repository.githubuser.GithubUserRepository
import com.ngapp.metanmobile.core.model.githubuser.GithubUserResource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class TestGithubUserRepository : GithubUserRepository {

    /**
     * The backing hot flow for the github user for testing.
     *
     * Seeded with `null` so collectors get a value right away, matching the Room-backed
     * production repository — see [TestLocationsRepository] for why an un-seeded
     * `MutableSharedFlow(replay = 1)` is a trap for anything that `combine`s this with another
     * flow.
     */
    private val githubUserFlow =
        MutableSharedFlow<GithubUserResource?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
            tryEmit(null)
        }

    /**
     * A test-only API to allow controlling the github user from tests.
     */
    fun sendGithubUser(githubUser: GithubUserResource?) {
        githubUserFlow.tryEmit(githubUser)
    }

    override fun getGithubUser(): Flow<GithubUserResource?> = githubUserFlow

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
