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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.domain.repository.news.NewsRepository
import com.ngapp.metanmobile.core.domain.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.ui.ShareManager
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailAction
import com.ngapp.metanmobile.feature.news.detail.state.NewsDetailUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** State holder for the News detail screen. */
class NewsDetailViewModel(
    private val newsRepository: NewsRepository,
    private val userDataRepository: UserDataRepository,
    private val shareManager: ShareManager,
) : ViewModel() {
    private val newsId = MutableStateFlow("")
    private var lastNews: NewsResource? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = newsId
        .filter { it.isNotEmpty() }
        .flatMapLatest { id -> newsRepository.getNewsResource(id) }
        .map<NewsResource, NewsDetailUiState> { news ->
            lastNews = news
            NewsDetailUiState.Success(news)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsDetailUiState.Loading)

    fun dispatch(action: NewsDetailAction) {
        when (action) {
            is NewsDetailAction.SetNewsId -> onSetNewsId(action.newsId)
            NewsDetailAction.ShareNews -> lastNews?.let(shareManager::createShareNewsIntent)
        }
    }

    private fun onSetNewsId(id: String) {
        if (newsId.value == id) return
        newsId.value = id
        viewModelScope.launch { userDataRepository.setNewsResourceViewed(id, true) }
    }
}
