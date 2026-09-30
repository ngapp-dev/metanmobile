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

package com.ngapp.metanmobile.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.ngapp.metanmobile.core.analytics.AnalyticsEvent
import com.ngapp.metanmobile.core.analytics.AnalyticsEvent.ParamKeys
import com.ngapp.metanmobile.core.analytics.AnalyticsEvent.Param
import com.ngapp.metanmobile.core.analytics.AnalyticsEvent.Types
import com.ngapp.metanmobile.core.analytics.AnalyticsHelper
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper

/**
 * Classes and functions associated with analytics events for the UI.
 */
fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.SCREEN_VIEW,
            extras = listOf(
                Param(ParamKeys.SCREEN_NAME, screenName),
            ),
        ),
    )
}

fun AnalyticsHelper.logNewsResourceOpened(newsId: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "news_resource_opened",
            extras = listOf(
                Param("opened_news_resource", newsId),
            ),
        ),
    )
}

fun AnalyticsHelper.logStationResourceOpened(stationCode: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "station_resource_opened",
            extras = listOf(
                Param("opened_station_resource", stationCode),
            ),
        ),
    )
}

fun AnalyticsHelper.logCareerResourceOpened(title: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "career_resource_opened",
            extras = listOf(
                Param("opened_career_resource", title),
            ),
        ),
    )
}

/**
 * A side-effect which records a screen view event.
 */
@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = DisposableEffect(Unit) {
    analyticsHelper.logScreenView(screenName)
    onDispose {}
}
