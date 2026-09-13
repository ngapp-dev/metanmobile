package com.ngapp.metanmobile.feature.news.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ngapp.metanmobile.feature.news.detail.NewsDetailRoute
import com.ngapp.metanmobile.feature.news.list.NewsRoute
import kotlinx.serialization.Serializable

/** Entry point used by the shared navigation graph on Android and iOS. */
@Composable
fun NewsScreen(onNewsDetailClick: (String) -> Unit = {}) = NewsRoute(onNewsDetailClick)

@Serializable
data class NewsDetailNavigation(val newsId: String)

fun NavController.navigateToNewsDetail(newsId: String, options: NavOptionsBuilder.() -> Unit = {}) =
    navigate(NewsDetailNavigation(newsId), options)

fun NavGraphBuilder.newsDetailScreen(onBackClick: () -> Unit) = composable<NewsDetailNavigation> { entry ->
    NewsDetailRoute(entry.toRoute<NewsDetailNavigation>().newsId, onBackClick)
}
