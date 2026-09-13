package com.ngapp.metanmobile.feature.news.list.state

import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig

sealed interface NewsUiState {
    data object Loading : NewsUiState
    data class Success(
        val pinnedNews: List<UserNewsResource>,
        val news: List<UserNewsResource>,
        val sorting: NewsSortingConfig,
    ) : NewsUiState
}
