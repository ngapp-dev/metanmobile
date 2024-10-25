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

package com.ngapp.metanmobile.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.feature.favorites.navigation.FavoritesNavigation
import com.ngapp.metanmobile.feature.home.navigation.HomeScreenNavigation
import com.ngapp.metanmobile.feature.news.list.navigation.NewsNavigation
import com.ngapp.metanmobile.feature.stations.list.navigation.StationsNavigation
import kotlin.reflect.KClass
import com.ngapp.metanmobile.core.designsystem.R as DesignsystemR

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
    val route: KClass<*>,
) {
    HOME(
        selectedIcon = MMIcons.HomeFilled,
        unselectedIcon = MMIcons.HomeOutlined,
        iconTextId = DesignsystemR.string.core_designsystem_bottom_menu_home,
        route = HomeScreenNavigation::class,
    ),
    STATIONS(
        selectedIcon = MMIcons.StationsFilled,
        unselectedIcon = MMIcons.StationsOutlined,
        iconTextId = DesignsystemR.string.core_designsystem_bottom_menu_stations,
        route = StationsNavigation::class,
    ),
    NEWS(
        selectedIcon = MMIcons.NewsFilled,
        unselectedIcon = MMIcons.NewsOutlined,
        iconTextId = DesignsystemR.string.core_designsystem_bottom_menu_news,
        route = NewsNavigation::class
    ),
    FAVORITES(
        selectedIcon = MMIcons.FavoritesFilled,
        unselectedIcon = MMIcons.FavoritesOutlined,
        iconTextId = DesignsystemR.string.core_designsystem_bottom_menu_favorites,
        route = FavoritesNavigation::class
    )
}