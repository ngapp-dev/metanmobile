package com.ngapp.metanmobile.feature.careers.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.ngapp.metanmobile.feature.careers.CareersRoute
import kotlinx.serialization.Serializable

@Serializable data object CareersNavigation
fun NavController.navigateToCareers(options: NavOptionsBuilder.() -> Unit = {}) = navigate(CareersNavigation, options)
fun NavGraphBuilder.careersScreen(onBackClick: () -> Unit) = composable<CareersNavigation> { CareersRoute(onBackClick) }
