package com.ngapp.metanmobile.feature.home.navigation

import androidx.compose.runtime.Composable
import com.ngapp.metanmobile.feature.home.HomeRoute

@Composable
fun HomeScreen(
    onNewsClick: () -> Unit = {},
    onNewsDetailClick: (String) -> Unit = {},
    onFaqClick: () -> Unit = {},
    onCareersClick: () -> Unit = {},
    onCabinetClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
) = HomeRoute(onNewsClick, onNewsDetailClick, onFaqClick, onCareersClick, onCabinetClick, onMenuClick)
