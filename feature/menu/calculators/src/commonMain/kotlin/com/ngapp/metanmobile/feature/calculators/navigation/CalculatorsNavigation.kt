package com.ngapp.metanmobile.feature.calculators.navigation
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.ngapp.metanmobile.feature.calculators.CalculatorsRoute
import kotlinx.serialization.Serializable
@Serializable data object CalculatorsNavigation
fun NavController.navigateToCalculators(options: NavOptionsBuilder.() -> Unit = {}) = navigate(CalculatorsNavigation, options)
fun NavGraphBuilder.calculatorsScreen(onBackClick: () -> Unit) = composable<CalculatorsNavigation> { CalculatorsRoute(onBackClick) }
