package com.ngapp.metanmobile.feature.about.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.ngapp.metanmobile.feature.about.AboutRoute
import kotlinx.serialization.Serializable

@Serializable data object AboutNavigation
fun NavController.navigateToAbout(options: NavOptionsBuilder.() -> Unit = {}) = navigate(AboutNavigation, options)
fun NavGraphBuilder.aboutScreen(onBackClick: () -> Unit) = composable<AboutNavigation> { AboutRoute(onBackClick) }
