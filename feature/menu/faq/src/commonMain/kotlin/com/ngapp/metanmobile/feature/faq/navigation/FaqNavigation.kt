package com.ngapp.metanmobile.feature.faq.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.ngapp.metanmobile.feature.faq.FaqRoute
import kotlinx.serialization.Serializable

@Serializable data object FaqNavigation
fun NavController.navigateToFaq(options: NavOptionsBuilder.() -> Unit = {}) = navigate(FaqNavigation, options)
fun NavGraphBuilder.faqScreen(onBackClick: () -> Unit) = composable<FaqNavigation> { FaqRoute(onBackClick) }
