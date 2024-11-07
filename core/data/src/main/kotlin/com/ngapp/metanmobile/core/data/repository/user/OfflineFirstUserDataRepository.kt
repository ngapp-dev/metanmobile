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

package com.ngapp.metanmobile.core.data.repository.user

import com.ngapp.metanmobile.core.analytics.AnalyticsHelper
import com.ngapp.metanmobile.core.data.repository.logDarkThemeConfigChanged
import com.ngapp.metanmobile.core.data.repository.logLanguageConfigChanged
import com.ngapp.metanmobile.core.data.repository.logOnboardingStateChanged
import com.ngapp.metanmobile.core.data.repository.logStationResourceFavoriteToggled
import com.ngapp.metanmobile.core.datastore.MetanMobilePreferencesDataSource
import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineFirstUserDataRepository @Inject constructor(
    private val preferencesDataSource: MetanMobilePreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper,
) : UserDataRepository {

    override val userData: Flow<UserData> = preferencesDataSource.userData

    override suspend fun setStationResourceFavorite(stationCode: String, favorite: Boolean) {
        preferencesDataSource.setStationResourceFavorite(stationCode, favorite)
        analyticsHelper.logStationResourceFavoriteToggled(
            stationResourceCode = stationCode, isFavorite = favorite,
        )
    }

    override suspend fun setLanguageConfig(languageConfig: LanguageConfig) {
        preferencesDataSource.setLanguageConfig(languageConfig)
        analyticsHelper.logLanguageConfigChanged(languageConfig.name)
    }

    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        preferencesDataSource.setNewsResourceViewed(newsResourceId, viewed)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        preferencesDataSource.setDarkThemeConfig(darkThemeConfig)
        analyticsHelper.logDarkThemeConfigChanged(darkThemeConfig.name)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        preferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
        analyticsHelper.logOnboardingStateChanged(shouldHideOnboarding)
    }

    override suspend fun setNewsSortingConfig(newsSortingConfig: NewsSortingConfig) {
        preferencesDataSource.setNewsSortingConfig(newsSortingConfig)
    }

    override suspend fun setStationSortingConfig(stationSortingConfig: StationSortingConfig) {
        preferencesDataSource.setStationSortingConfig(stationSortingConfig)
    }

    override suspend fun updateTotalUsageTime(usageTime: Long) {
        preferencesDataSource.updateTotalUsageTime(usageTime)
    }

    override suspend fun setReviewShown(shown: Boolean) {
        preferencesDataSource.setReviewShown(shown)
    }

    override suspend fun setHomeReorderableList(homeReorderableList: List<HomeContentItem>) {
        preferencesDataSource.setHomeReorderableList(homeReorderableList.map { it.name })
    }
}
