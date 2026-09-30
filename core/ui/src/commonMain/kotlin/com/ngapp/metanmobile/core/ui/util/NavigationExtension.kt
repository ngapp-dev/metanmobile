package com.ngapp.metanmobile.core.ui.util

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import kotlin.reflect.KType

/** A [composable] with a left-slide-in / right-slide-out transition, matching master's push feel. */
inline fun <reified T : Any> NavGraphBuilder.slideInLeftComposable(
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = {
        slideInHorizontally(
            animationSpec = tween(250),
            initialOffsetX = { it }
        )
    },
    noinline exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = {
        slideOutHorizontally(
            animationSpec = tween(250),
            targetOffsetX = { -it }
        )
    },
    noinline popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = {
        slideInHorizontally(
            animationSpec = tween(250),
            initialOffsetX = { -it })
    },
    noinline popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = {
        slideOutHorizontally(
            animationSpec = tween(250),
            targetOffsetX = { it })
    },
    noinline sizeTransform: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? = null,
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable<T>(
        typeMap = typeMap,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        sizeTransform = sizeTransform,
        content = content
    )
}
