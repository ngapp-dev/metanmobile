package com.ngapp.metanmobile.core.domain.repository.user

import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun setStationResourceFavorite(stationCode: String, favorite: Boolean)
    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean)
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)
    suspend fun setNewsSortingConfig(newsSortingConfig: NewsSortingConfig)
    suspend fun setStationSortingConfig(stationSortingConfig: StationSortingConfig)
    suspend fun updateTotalUsageTime(usageTime: Long)
    suspend fun setReviewShown(isShown: Boolean)
    suspend fun setHomeReorderableList(homeReorderableList: List<HomeContentItem>)
    suspend fun setHomeLastNewsExpanded(isExpanded: Boolean)
}
