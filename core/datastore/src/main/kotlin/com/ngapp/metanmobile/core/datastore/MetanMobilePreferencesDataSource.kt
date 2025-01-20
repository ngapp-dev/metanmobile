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

package com.ngapp.metanmobile.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.ngapp.metanmobile.core.datastore.model.asHomeContentItem
import com.ngapp.metanmobile.core.datastore.model.toModel
import com.ngapp.metanmobile.core.datastore.model.toProto
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.UserData
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class MetanMobilePreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data
        .map {
            UserData(
                favoriteStationResources = it.favoriteStationResourceCodesMap.keys,
                viewedNewsResources = it.viewedNewsResourceIdsMap.keys,
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                        -> DarkThemeConfig.FOLLOW_SYSTEM

                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
                languageConfig = when (it.languageConfig) {
                    null,
                    LanguageConfigProto.LANGUAGE_RU,
                    LanguageConfigProto.UNRECOGNIZED,
                    LanguageConfigProto.LANGUAGE_UNSPECIFIED,
                        -> LanguageConfig.RU

                    LanguageConfigProto.LANGUAGE_BE -> LanguageConfig.BE
                    LanguageConfigProto.LANGUAGE_EN -> LanguageConfig.EN
                },
                shouldHideOnboarding = it.shouldHideOnboarding,
                newsSortingConfig = it.newsSortingConfig.toModel(),
                stationSortingConfig = it.stationSortingConfig.toModel(),
                isReviewShown = it.isReviewShown,
                totalUsageTime = it.totalUsageTime,
                homeReorderableList = it.homeReorderableList.map(String::asHomeContentItem),
                homeLastNewsExpanded = it.isHomeLastNewsExpanded,
                stationDetailCode = it.stationDetailCode,
            )
        }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM ->
                        DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM

                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun setLanguageConfig(languageConfig: LanguageConfig) {
        userPreferences.updateData {
            it.copy {
                this.languageConfig = when (languageConfig) {
                    LanguageConfig.RU -> LanguageConfigProto.LANGUAGE_RU
                    LanguageConfig.BE -> LanguageConfigProto.LANGUAGE_BE
                    LanguageConfig.EN -> LanguageConfigProto.LANGUAGE_EN
                }
            }
        }
    }

    suspend fun setStationResourceFavorite(stationResourceCode: String, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        favoriteStationResourceCodes.put(stationResourceCode, true)
                    } else {
                        favoriteStationResourceCodes.remove(stationResourceCode)
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("MetanMobilePreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        setNewsResourcesViewed(listOf(newsResourceId), viewed)
    }

    private suspend fun setNewsResourcesViewed(newsResourceIds: List<String>, viewed: Boolean) {
        userPreferences.updateData { prefs ->
            prefs.copy {
                newsResourceIds.forEach { id ->
                    if (viewed) {
                        viewedNewsResourceIds.put(id, true)
                    } else {
                        viewedNewsResourceIds.remove(id)
                    }
                }
            }
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy { this.shouldHideOnboarding = shouldHideOnboarding }
        }
    }

    suspend fun setNewsSortingConfig(newsSortingConfig: NewsSortingConfig) {
        userPreferences.updateData {
            it.copy {
                this.newsSortingConfig = NewsSortingConfigProto.newBuilder()
                    .setSortTypeConfig(newsSortingConfig.sortingType.toProto())
                    .setSortOrderConfig(newsSortingConfig.sortingOrder.toProto())
                    .build()
            }
        }
    }

    suspend fun setStationSortingConfig(stationSortingConfig: StationSortingConfig) {
        userPreferences.updateData {
            it.copy {
                this.stationSortingConfig = StationSortingConfigProto.newBuilder()
                    .setSortTypeConfig(stationSortingConfig.sortingType.toProto())
                    .setSortOrderConfig(stationSortingConfig.sortingOrder.toProto())
                    .addAllActiveStationTypesConfig(stationSortingConfig.activeStationTypes.map { it.toProto() })
                    .build()
            }
        }
    }

    suspend fun updateTotalUsageTime(totalUsageTime: Long) {
        userPreferences.updateData {
            it.copy { this.totalUsageTime = totalUsageTime }
        }
    }

    suspend fun setReviewShown(isReviewShown: Boolean) {
        userPreferences.updateData {
            it.copy { this.isReviewShown = isReviewShown }
        }
    }

    suspend fun setHomeReorderableList(homeReorderableList: List<String>) {
        userPreferences.updateData {
            it.copy {
                this.homeReorderable.clear()
                this.homeReorderable.addAll(homeReorderableList)
            }
        }
    }

    suspend fun setHomeExpandedLastNews(isExpanded: Boolean) {
        userPreferences.updateData {
            it.copy { this.isHomeLastNewsExpanded = isExpanded }
        }
    }

    suspend fun setStationDetailCode(stationDetailCode: String) {
        userPreferences.updateData {
            it.copy { this.stationDetailCode = stationDetailCode }
        }
    }
}
