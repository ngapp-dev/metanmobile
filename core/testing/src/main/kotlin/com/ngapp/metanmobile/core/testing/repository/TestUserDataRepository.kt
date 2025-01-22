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

package com.ngapp.metanmobile.core.testing.repository

import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.UserData
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

val emptyUserData = UserData(
    favoriteStationResources = emptySet(),
    viewedNewsResources = emptySet(),
    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    shouldHideOnboarding = false,
    newsSortingConfig = NewsSortingConfig.init(),
    stationSortingConfig = StationSortingConfig.init(),
    totalUsageTime = 0L,
    homeReorderableList = emptyList(),
    homeLastNewsExpanded = true,
    isReviewShown = false,
)

class TestUserDataRepository : UserDataRepository {
    /**
     * The backing hot flow for the list of followed topic ids for testing.
     */
    private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = DROP_OLDEST)

    private val currentUserData get() = _userData.replayCache.firstOrNull() ?: emptyUserData

    override val userData: Flow<UserData> = _userData.filterNotNull()

    override suspend fun setStationResourceFavorite(stationCode: String, favorite: Boolean) {
        currentUserData.let { current ->
            val favoriteStations = if (favorite) {
                current.favoriteStationResources + stationCode
            } else {
                current.favoriteStationResources - stationCode
            }

            _userData.tryEmit(current.copy(favoriteStationResources = favoriteStations))
        }
    }

    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    viewedNewsResources =
                    if (viewed) {
                        current.viewedNewsResources + newsResourceId
                    } else {
                        current.viewedNewsResources - newsResourceId
                    },
                ),
            )
        }
    }


    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(darkThemeConfig = darkThemeConfig))
        }
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(shouldHideOnboarding = shouldHideOnboarding))
        }
    }

    override suspend fun setNewsSortingConfig(newsSortingConfig: NewsSortingConfig) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(newsSortingConfig = newsSortingConfig))
        }
    }

    override suspend fun setStationSortingConfig(stationSortingConfig: StationSortingConfig) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(stationSortingConfig = stationSortingConfig))
        }
    }

    override suspend fun updateTotalUsageTime(usageTime: Long) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(totalUsageTime = usageTime))
        }
    }

    override suspend fun setReviewShown(isShown: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(isReviewShown = isShown))
        }
    }

    override suspend fun setHomeReorderableList(homeReorderableList: List<HomeContentItem>) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(homeReorderableList = homeReorderableList))
        }
    }

    override suspend fun setHomeLastNewsExpanded(isExpanded: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(homeLastNewsExpanded = isExpanded))
        }
    }

    /**
     * A test-only API to allow setting of user data directly.
     */
    fun setUserData(userData: UserData) {
        _userData.tryEmit(userData)
    }
}
