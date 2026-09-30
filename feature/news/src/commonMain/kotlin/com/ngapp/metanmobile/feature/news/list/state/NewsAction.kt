package com.ngapp.metanmobile.feature.news.list.state

import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig

sealed interface NewsAction {
    data class UpdateSearchQuery(val value: String) : NewsAction
    data class UpdateSortingConfig(val value: NewsSortingConfig) : NewsAction
    data class SetSortingVisible(val visible: Boolean) : NewsAction
}
