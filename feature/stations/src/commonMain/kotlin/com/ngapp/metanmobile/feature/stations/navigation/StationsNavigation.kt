package com.ngapp.metanmobile.feature.stations.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ngapp.metanmobile.feature.stations.StationsRoute

@Composable
fun StationsScreen(
    onNewsDetailClick: (String) -> Unit = {},
    onShowBottomBar: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) = StationsRoute(onNewsDetailClick, onShowBottomBar, modifier)
