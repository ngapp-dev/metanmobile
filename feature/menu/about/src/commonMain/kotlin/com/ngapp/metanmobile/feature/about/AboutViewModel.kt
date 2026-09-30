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

package com.ngapp.metanmobile.feature.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.githubuser.GithubUserRepository
import com.ngapp.metanmobile.core.data.util.SyncManager
import com.ngapp.metanmobile.feature.about.state.AboutUiState
import com.ngapp.metanmobile.feature.about.state.AboutUiState.Success
import com.ngapp.metanmobile.feature.about.state.AboutUiState.Loading
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Not part of [com.ngapp.metanmobile.core.data.sync.DataSyncCoordinator] — see the note on that
 * class for why. This screen is the only reader of [GithubUserRepository], so it syncs it itself
 * on open instead; [Synchronizer] is a stateless marker interface (`sync()` just forwards to
 * `syncWith(this)`), so a throwaway instance is all `updateSingleDataSync` needs.
 */
private object AboutScreenSynchronizer : Synchronizer

class AboutViewModel(
    private val githubUserRepository: GithubUserRepository,
    syncManager: SyncManager,
) : ViewModel() {

    init {
        viewModelScope.launch {
            with(AboutScreenSynchronizer) { githubUserRepository.sync() }
        }
    }

    val uiState: StateFlow<AboutUiState> = githubUserRepository.getGithubUser()
        .map(::Success)
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = Loading,
        )

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = false,
        )
}
