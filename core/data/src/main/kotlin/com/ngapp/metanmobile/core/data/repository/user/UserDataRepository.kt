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

import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>

    /**
     * Updates the favorite status for a station resource
     */
    suspend fun setStationResourceFavorite(stationCode: String, favorite: Boolean)

    /**
     * Updates the language configuration
     */
    suspend fun setLanguageConfig(languageConfig: LanguageConfig)

    /**
     * Updates the viewed status for a news resource
     */
    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean)

    /**
     * Sets the desired dark theme config.
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    /**
     * Sets whether the user has completed the onboarding process.
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)

    /**
     * Sets news resources sorting configuration.
     */
    suspend fun setNewsSortingConfig(newsSortingConfig: NewsSortingConfig)

    /**
     * Sets station resources sorting configuration.
     */
    suspend fun setStationSortingConfig(stationSortingConfig: StationSortingConfig)

    /**
     * Updates the total usage time in milliseconds.
     */
    suspend fun updateTotalUsageTime(usageTime: Long)

    /**
     * Sets whether the review has been shown.
     */
    suspend fun setReviewShown(shown: Boolean)

    /**
     * Sets order of home reorderable list.
     */
    suspend fun setHomeReorderableList(homeReorderableList: List<HomeContentItem>)
}
