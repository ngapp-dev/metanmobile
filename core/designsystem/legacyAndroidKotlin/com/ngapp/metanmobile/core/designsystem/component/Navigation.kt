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

package com.ngapp.metanmobile.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.designsystem.theme.navigationBarIndicatorColor

/**
 * Metan Mobile navigation bar item with icon and label content slots. Wraps Material 3
 * [NavigationBarItem].
 *
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param icon The item icon content.
 * @param modifier Modifier to be applied to this item.
 * @param selectedIcon The item icon content when selected.
 * @param enabled controls the enabled state of this item. When `false`, this item will not be
 * clickable and will appear disabled to accessibility services.
 * @param label The item text label content.
 * @param alwaysShowLabel Whether to always show the label for this item. If false, the label will
 * only be shown when this item is selected.
 */
@Composable
fun RowScope.MMNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MMNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = MMNavigationDefaults.navigationContentColor(),
            selectedTextColor = MMNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = MMNavigationDefaults.navigationContentColor(),
            indicatorColor = MMColors.navigationBarIndicatorColor,
        ),
    )
}

/**
 * Metan Mobile navigation bar with content slot. Wraps Material 3 [NavigationBar].
 *
 * @param modifier Modifier to be applied to the navigation bar.
 * @param content Destinations inside the navigation bar. This should contain multiple
 * [NavigationBarItem]s.
 */
@Composable
fun MMNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.onSurface,
        contentColor = MMNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content,
    )
}

/**
 * Metan Mobile navigation rail item with icon and label content slots. Wraps Material 3
 * [NavigationRailItem].
 *
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param icon The item icon content.
 * @param modifier Modifier to be applied to this item.
 * @param selectedIcon The item icon content when selected.
 * @param enabled controls the enabled state of this item. When `false`, this item will not be
 * clickable and will appear disabled to accessibility services.
 * @param label The item text label content.
 * @param alwaysShowLabel Whether to always show the label for this item. If false, the label will
 * only be shown when this item is selected.
 */
@Composable
fun MMNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = MMNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = MMNavigationDefaults.navigationContentColor(),
            selectedTextColor = MMNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = MMNavigationDefaults.navigationContentColor(),
            indicatorColor = MMColors.navigationBarIndicatorColor,
        ),
    )
}

/**
 * Metan Mobile navigation rail with header and content slots. Wraps Material 3 [NavigationRail].
 *
 * @param modifier Modifier to be applied to the navigation rail.
 * @param header Optional header that may hold a floating action button or a logo.
 * @param content Destinations inside the navigation rail. This should contain multiple
 * [NavigationRailItem]s.
 */
@Composable
fun MMNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MMNavigationDefaults.navigationContentColor(),
        header = header,
        content = content,
    )
}

/**
 * Metan Mobile navigation suite scaffold with item and content slots.
 * Wraps Material 3 [NavigationSuiteScaffold].
 *
 * @param modifier Modifier to be applied to the navigation suite scaffold.
 * @param navigationSuiteItems A slot to display multiple items via [NiaNavigationSuiteScope].
 * @param windowAdaptiveInfo The window adaptive info.
 * @param content The app content inside the scaffold.
 */
@Composable
fun MMNavigationSuiteScaffold(
    navigationSuiteItems: NiaNavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    showBottomBar: Boolean,
    adsContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
    val navigationSuiteItemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MMNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = MMNavigationDefaults.navigationContentColor(),
            selectedTextColor = MMNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = MMNavigationDefaults.navigationContentColor(),
            indicatorColor = MMNavigationDefaults.navigationIndicatorColor(),
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = MMNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = MMNavigationDefaults.navigationContentColor(),
            selectedTextColor = MMNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = MMNavigationDefaults.navigationContentColor(),
            indicatorColor = MMNavigationDefaults.navigationIndicatorColor(),
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = MMNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = MMNavigationDefaults.navigationContentColor(),
            selectedTextColor = MMNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = MMNavigationDefaults.navigationContentColor(),
        ),
    )
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            NiaNavigationSuiteScope(
                navigationSuiteScope = this,
                navigationSuiteItemColors = navigationSuiteItemColors,
            ).run(navigationSuiteItems)
        },
        layoutType = if (showBottomBar) layoutType else NavigationSuiteType.None,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MMNavigationDefaults.navigationContainerColor(),
            navigationBarContentColor = MMNavigationDefaults.navigationContentColor(),
            navigationRailContainerColor = MMNavigationDefaults.navigationContainerColor(),
        ),
        modifier = modifier,
    ) {
        Column {
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onSurface)
                ) {
                    adsContent()
                }
                MMDivider(color = MaterialTheme.colorScheme.surfaceTint)
            }
        }
    }
}

/**
 * A wrapper around [NavigationSuiteScope] to declare navigation items.
 */
class NiaNavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors,
) {
    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        selectedIcon: @Composable () -> Unit = icon,
        label: @Composable (() -> Unit)? = null,
    ) = navigationSuiteScope.item(
        selected = selected,
        onClick = onClick,
        icon = { if (selected) selectedIcon() else icon() },
        label = label,
        colors = navigationSuiteItemColors,
        modifier = modifier,
    )
}

@PreviewScreenSizes
@Composable
fun MetanMobileNavigationBarPreview() {
    val items = listOf("Home", "Stations", "News", "Favorites")
    val icons = listOf(
        MMIcons.HomeFilled,
        MMIcons.StationsFilled,
        MMIcons.NewsFilled,
        MMIcons.FavoritesFilled,
    )
    val selectedIcons = listOf(
        MMIcons.HomeOutlined,
        MMIcons.StationsOutlined,
        MMIcons.NewsOutlined,
        MMIcons.FavoritesOutlined,
    )

    MMTheme {
        MMNavigationBar {
            items.forEachIndexed { index, item ->
                MMNavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = selectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = index == 0,
                    onClick = {},
                )
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun MetanMobileNavigationRailPreview() {
    val items = listOf("Home", "Stations", "News", "Favorites")
    val icons = listOf(
        MMIcons.HomeFilled,
        MMIcons.StationsFilled,
        MMIcons.NewsFilled,
        MMIcons.FavoritesFilled,
    )
    val selectedIcons = listOf(
        MMIcons.HomeOutlined,
        MMIcons.StationsOutlined,
        MMIcons.NewsOutlined,
        MMIcons.FavoritesOutlined,
    )

    MMTheme {
        MMNavigationRail {
            items.forEachIndexed { index, item ->
                MMNavigationRailItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = selectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = index == 0,
                    onClick = {},
                )
            }
        }
    }
}

/**
 * Metan Mobile navigation default values.
 */
object MMNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationContainerColor() = MaterialTheme.colorScheme.onSurface

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.tertiaryContainer
}
