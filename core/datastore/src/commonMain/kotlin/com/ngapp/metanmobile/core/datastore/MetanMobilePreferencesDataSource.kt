package com.ngapp.metanmobile.core.datastore

import androidx.datastore.core.DataStore
import com.ngapp.metanmobile.core.datastore.shared.DarkThemeConfigProto
import com.ngapp.metanmobile.core.datastore.shared.NewsSortingConfigProto
import com.ngapp.metanmobile.core.datastore.shared.StationSortingConfigProto
import com.ngapp.metanmobile.core.datastore.shared.UserPreferences
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MetanMobilePreferencesDataSource(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data.map { preferences ->
        UserData(
            favoriteStationResources = preferences.favorite_station_resource_codes.keys,
            viewedNewsResources = preferences.viewed_news_resource_ids.keys,
            darkThemeConfig = preferences.dark_theme_config.toModel(),
            shouldHideOnboarding = preferences.should_hide_onboarding,
            newsSortingConfig = (preferences.news_sorting_config ?: NewsSortingConfigProto()).toModel(),
            stationSortingConfig = (preferences.station_sorting_config ?: StationSortingConfigProto()).toModel(),
            isReviewShown = preferences.is_review_shown,
            totalUsageTime = preferences.total_usage_time,
            homeReorderableList = preferences.home_reorderable.mapNotNull(String::asHomeContentItemOrNull),
            homeLastNewsExpanded = preferences.is_home_last_news_expanded,
        )
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy(dark_theme_config = darkThemeConfig.toProto())
        }
    }

    suspend fun setStationResourceFavorite(stationResourceCode: String, isFavorite: Boolean) {
        userPreferences.updateData { preferences ->
            val favorites = if (isFavorite) {
                preferences.favorite_station_resource_codes + (stationResourceCode to true)
            } else {
                preferences.favorite_station_resource_codes - stationResourceCode
            }
            preferences.copy(favorite_station_resource_codes = favorites)
        }
    }

    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        userPreferences.updateData { preferences ->
            val viewedIds = if (viewed) {
                preferences.viewed_news_resource_ids + (newsResourceId to true)
            } else {
                preferences.viewed_news_resource_ids - newsResourceId
            }
            preferences.copy(viewed_news_resource_ids = viewedIds)
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy(should_hide_onboarding = shouldHideOnboarding)
        }
    }

    suspend fun setNewsSortingConfig(newsSortingConfig: NewsSortingConfig) {
        userPreferences.updateData {
            it.copy(news_sorting_config = newsSortingConfig.toProto())
        }
    }

    suspend fun setStationSortingConfig(stationSortingConfig: StationSortingConfig) {
        userPreferences.updateData {
            it.copy(station_sorting_config = stationSortingConfig.toProto())
        }
    }

    suspend fun updateTotalUsageTime(totalUsageTime: Long) {
        userPreferences.updateData {
            it.copy(total_usage_time = totalUsageTime)
        }
    }

    suspend fun setReviewShown(isReviewShown: Boolean) {
        userPreferences.updateData {
            it.copy(is_review_shown = isReviewShown)
        }
    }

    suspend fun setHomeReorderableList(homeReorderableList: List<String>) {
        userPreferences.updateData {
            it.copy(home_reorderable = homeReorderableList)
        }
    }

    suspend fun setHomeExpandedLastNews(isExpanded: Boolean) {
        userPreferences.updateData {
            it.copy(is_home_last_news_expanded = isExpanded)
        }
    }

    // Не часть [userData] нарочно: эта версия меняется на каждом успешном фоновом
    // синке (минимум раз в 15 минут, см. спеку синхронизации), а [userData] — общий
    // Flow, на который подписан почти весь UI. Если завести sync_version туда,
    // каждый синк переэмитил бы его и лишний раз перерисовывал весь экран.
    suspend fun getSyncVersion(): Long = userPreferences.data.first().sync_version

    suspend fun setSyncVersion(version: Long) {
        userPreferences.updateData {
            it.copy(sync_version = version)
        }
    }

    private fun DarkThemeConfigProto.toModel(): DarkThemeConfig = when (this) {
        DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
        DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
        DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
        DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
            -> DarkThemeConfig.FOLLOW_SYSTEM
    }

    private fun DarkThemeConfig.toProto(): DarkThemeConfigProto = when (this) {
        DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
        DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
        DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
    }
}
