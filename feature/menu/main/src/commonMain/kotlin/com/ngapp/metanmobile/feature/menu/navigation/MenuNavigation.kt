package com.ngapp.metanmobile.feature.menu.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.ngapp.metanmobile.core.ui.util.slideInLeftComposable
import com.ngapp.metanmobile.feature.menu.MenuRoute
import kotlinx.serialization.Serializable

@Serializable
data object MenuNavigation

fun NavController.navigateToMenu(options: NavOptionsBuilder.() -> Unit = {}) =
    navigate(MenuNavigation, options)

fun NavGraphBuilder.menuScreen(
    onContactsClick: () -> Unit,
    onFaqClick: () -> Unit,
    onCalculatorsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLegalClick: () -> Unit,
    onCareersClick: () -> Unit,
    onBackClick: () -> Unit,
) = slideInLeftComposable<MenuNavigation> {
    MenuRoute(onContactsClick, onFaqClick, onCalculatorsClick, onAboutClick, onLegalClick, onCareersClick, onBackClick)
}
