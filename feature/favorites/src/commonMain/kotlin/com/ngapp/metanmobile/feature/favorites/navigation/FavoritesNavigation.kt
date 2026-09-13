package com.ngapp.metanmobile.feature.favorites.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ngapp.metanmobile.feature.favorites.FavoritesRoute

@Composable
fun FavoritesScreen(
    onNewsDetailClick: (String) -> Unit = {},
    onShowBottomBar: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) = FavoritesRoute(onNewsDetailClick, onShowBottomBar, modifier)
