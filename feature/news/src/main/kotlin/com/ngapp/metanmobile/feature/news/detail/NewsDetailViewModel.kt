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

package com.ngapp.metanmobile.feature.news.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ngapp.metanmobile.core.data.repository.news.NewsRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.ui.ShareManager
import com.ngapp.metanmobile.feature.news.detail.navigation.NewsDetailNavigation
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailAction
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailUiState
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailUiState.Loading
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    newsRepository: NewsRepository,
    private val userDataRepository: UserDataRepository,
    private val shareManager: ShareManager,
) : ViewModel() {

    private val newsId = savedStateHandle.toRoute<NewsDetailNavigation>().newsId

    val uiState: StateFlow<NewsDetailUiState> = newsRepository.getNewsResource(newsId)
        .map<NewsResource, NewsDetailUiState>(::Success)
        .onEach { onSetNewsResourceViewed(newsId) }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = Loading
        )

    fun triggerAction(action: NewsDetailAction) {
        when (action) {
            is NewsDetailAction.ShareNews -> onShareNews(action.news)
        }
    }

    private suspend fun onSetNewsResourceViewed(newsResourceId: String) {
        userDataRepository.setNewsResourceViewed(newsResourceId, true)
    }

    private fun onShareNews(news: NewsResource?) {
        shareManager.createShareNewsIntent(news)
    }
}