package com.ngapp.metanmobile.feature.cabinet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.ngapp.metanmobile.feature.cabinet.CabinetRoute
import kotlinx.serialization.Serializable

@Composable
fun CabinetScreen(onBackClick: () -> Unit = {}) = CabinetRoute(onBackClick)

@Serializable
data object CabinetNavigation

fun NavController.navigateToCabinet(options: NavOptionsBuilder.() -> Unit = {}) = navigate(CabinetNavigation, options)

fun NavGraphBuilder.cabinetScreen(onBackClick: () -> Unit) = composable<CabinetNavigation> { CabinetRoute(onBackClick) }
