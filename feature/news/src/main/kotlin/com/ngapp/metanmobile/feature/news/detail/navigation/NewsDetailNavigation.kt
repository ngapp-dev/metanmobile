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

package com.ngapp.metanmobile.feature.news.detail.navigation

import androidx.annotation.VisibleForTesting
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.ngapp.metanmobile.feature.news.detail.NewsDetailRoute
import kotlinx.serialization.Serializable

@VisibleForTesting
internal const val NEWS_DETAIL_ID_ARG = "newsId"
var DEEP_LINK_URI_PATTERN = "https://metan.by/news/by/{$NEWS_DETAIL_ID_ARG}"

fun NavController.navigateToNewsDetail(
    newsId: String, navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route = NewsDetailNavigation(newsId)) { navOptions() }
}

fun NavGraphBuilder.newsDetailScreen(onBackClick: () -> Unit) {
    composable<NewsDetailNavigation>(
        deepLinks = listOf(navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }),
    ) {
        NewsDetailRoute(onBackClick = onBackClick)
    }
}

@Serializable
data class NewsDetailNavigation(val newsId: String)
