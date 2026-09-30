package com.ngapp.metanmobile.feature.news.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.domain.repository.news.NewsResourceQuery
import com.ngapp.metanmobile.core.domain.repository.news.UserNewsResourceRepository
import com.ngapp.metanmobile.core.domain.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.feature.news.list.state.NewsAction
import com.ngapp.metanmobile.feature.news.list.state.NewsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Shared state holder for the original News feature behaviour. */
class NewsViewModel(
    private val userNewsRepository: UserNewsResourceRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val sortingVisible = MutableStateFlow(false)

    val search = searchQuery
    val isSortingVisible = sortingVisible

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = searchQuery.flatMapLatest { query ->
        combine(
            userNewsRepository.observeAll(NewsResourceQuery(filterNewsPinned = true)),
            userNewsRepository.observeAll(NewsResourceQuery(filterNewsPinned = false, searchQuery = query)),
            userDataRepository.userData,
        ) { pinned, news, userData ->
            NewsUiState.Success(pinned, news, userData.newsSortingConfig)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsUiState.Loading)

    fun dispatch(action: NewsAction) {
        when (action) {
            is NewsAction.UpdateSearchQuery -> searchQuery.value = action.value
            is NewsAction.SetSortingVisible -> sortingVisible.value = action.visible
            is NewsAction.UpdateSortingConfig -> updateSorting(action.value)
        }
    }

    private fun updateSorting(config: NewsSortingConfig) = viewModelScope.launch {
        userDataRepository.setNewsSortingConfig(config)
        sortingVisible.value = false
    }
}
