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

package com.ngapp.metanmobile.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    onPrimary = White,
    primaryContainer = DarkGrayBlue,
    onPrimaryContainer = LightBlue,
    secondary = DarkBlue,
    onSecondary = Black,
    secondaryContainer = Orange30,
    onSecondaryContainer = Orange90,
    tertiary = DarkGrayBlue,
    onTertiary = White,
    tertiaryContainer = Blue,
    onTertiaryContainer = Blue90,
    error = Red40,
    onError = White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = BackgroundDark,
    onBackground = BackgroundDark,
    surface = CardDark,
    onSurface = CardDark,
    surfaceVariant = DarkGrayBlue,
    onSurfaceVariant = White,
    inverseOnSurface = CardDark,
    inverseSurface = Gray600,
    surfaceContainerHigh = CardDark,
    outline = PurpleGray60,
    surfaceTint = darkGray,
)

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = Black,
    primaryContainer = LightBlue,
    onPrimaryContainer = Black,
    secondary = White,
    onSecondary = Black,
    secondaryContainer = DarkGreen30,
    onSecondaryContainer = DarkGreen90,
    tertiary = White,
    onTertiary = Black,
    tertiaryContainer = LightBlue,
    onTertiaryContainer = Teal90,
    error = Red40,
    onError = White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = BackgroundLight,
    onBackground = White,
    surface = White,
    onSurface = White,
    surfaceVariant = White,
    onSurfaceVariant = Black,
    inverseOnSurface = White,
    inverseSurface = Gray250,
    surfaceContainerHigh = White,
    outline = GreenGray60,
    surfaceTint = lightGray,
)

val MMColors: ColorScheme
    @Composable get() = MaterialTheme.colorScheme

val MMShapes: Shapes
    @Composable get() = MaterialTheme.shapes

val MMTypography: Typography
    @Composable get() = MaterialTheme.typography

@Composable
fun MMTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val defaultGradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        bottom = colorScheme.primaryContainer,
        container = colorScheme.surface,
    )
    val typography = if (darkTheme) {
        DarkTypography
    } else {
        LightTypography
    }

    CompositionLocalProvider(LocalGradientColors provides defaultGradientColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = Shapes,
            content = content
        )
    }
}