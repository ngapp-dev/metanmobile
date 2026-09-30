package com.ngapp.metanmobile.feature.home.state

import com.ngapp.metanmobile.core.model.home.HomeContentItem

sealed interface HomeAction {
    data class UpdateLocation(val hasPermissions: Boolean) : HomeAction
    data class EditUi(val isEditing: Boolean) : HomeAction
    data class ReorderList(val newOrder: List<HomeContentItem>) : HomeAction
    data object SaveUi : HomeAction
    data class ExpandLastNews(val expand: Boolean) : HomeAction
    data object RetrySync : HomeAction
}
