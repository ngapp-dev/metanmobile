package com.ngapp.metanmobile.composeapp.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import dev.icerock.moko.resources.StringResource

enum class TopLevelDestination(
    val route: String,
    val title: StringResource,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("home", SharedRes.strings.designsystem_bottom_menu_home, MMIcons.HomeFilled, MMIcons.HomeOutlined),
    STATIONS("stations", SharedRes.strings.designsystem_bottom_menu_stations, MMIcons.StationsFilled, MMIcons.StationsOutlined),
    NEWS("news", SharedRes.strings.designsystem_bottom_menu_news, MMIcons.NewsFilled, MMIcons.NewsOutlined),
    FAVORITES("favorites", SharedRes.strings.designsystem_bottom_menu_favorites, MMIcons.FavoritesFilled, MMIcons.FavoritesOutlined),
}
