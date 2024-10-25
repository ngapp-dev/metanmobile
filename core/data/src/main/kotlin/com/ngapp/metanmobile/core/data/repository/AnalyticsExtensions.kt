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

package com.ngapp.metanmobile.core.data.repository

import com.ngapp.metanmobile.core.analytics.AnalyticsEvent
import com.ngapp.metanmobile.core.analytics.AnalyticsEvent.Param
import com.ngapp.metanmobile.core.analytics.AnalyticsHelper

fun AnalyticsHelper.logStationResourceFavoriteToggled(
    stationResourceCode: String,
    isFavorite: Boolean,
) {
    val eventType = if (isFavorite) "station_resource_saved" else "station_resource_unsaved"
    val paramKey =
        if (isFavorite) "saved_station_resource_code" else "unsaved_station_resource_code"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = listOf(Param(key = paramKey, value = stationResourceCode)),
        ),
    )
}

fun AnalyticsHelper.logDarkThemeConfigChanged(darkThemeConfigName: String) =
    logEvent(
        AnalyticsEvent(
            type = "dark_theme_config_changed",
            extras = listOf(
                Param(key = "dark_theme_config", value = darkThemeConfigName),
            ),
        ),
    )

fun AnalyticsHelper.logLanguageConfigChanged(languageConfigName: String) =
    logEvent(
        AnalyticsEvent(
            type = "language_config_changed",
            extras = listOf(Param(key = "language_config_changed", value = languageConfigName)),
        ),
    )

fun AnalyticsHelper.logOnboardingStateChanged(shouldHideOnboarding: Boolean) {
    val eventType = if (shouldHideOnboarding) "onboarding_complete" else "onboarding_reset"
    logEvent(
        AnalyticsEvent(type = eventType),
    )
}
