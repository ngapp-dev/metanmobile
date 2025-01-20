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

package com.ngapp.metanmobile.core.data.test.repository

import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.datastore.MetanMobilePreferencesDataSource
import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FakeUserDataRepository @Inject constructor(
    private val preferencesDataSource: MetanMobilePreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> = preferencesDataSource.userData

    override suspend fun setStationResourceFavorite(stationCode: String, favorite: Boolean) {
        preferencesDataSource.setStationResourceFavorite(stationCode, favorite)
    }

    override suspend fun setLanguageConfig(languageConfig: LanguageConfig) {
        preferencesDataSource.setLanguageConfig(languageConfig)
    }

    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        preferencesDataSource.setNewsResourceViewed(newsResourceId, viewed)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        preferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        preferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
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

    override suspend fun setReviewShown(isShown: Boolean) {
        preferencesDataSource.setReviewShown(isShown)
    }

    override suspend fun setHomeReorderableList(homeReorderableList: List<HomeContentItem>) {
        preferencesDataSource.setHomeReorderableList(homeReorderableList.map { it.name })
    }

    override suspend fun setHomeLastNewsExpanded(isExpanded: Boolean) {
        preferencesDataSource.setHomeExpandedLastNews(isExpanded)
    }

    override suspend fun setStationDetailCode(stationCode: String) {
        preferencesDataSource.setStationDetailCode(stationCode)
    }
}
