/*
 * Copyright 2026 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.toolbarIconColor
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.isRenderEffectSupported
import com.kyant.backdrop.shadow.Shadow

/**
 * The screen content that glass elements of the current [MMScaffold] sample from, or null outside
 * of one (glass then falls back to a plain tinted surface).
 */
val LocalMMBackdrop = staticCompositionLocalOf<Backdrop?> { null }

/**
 * Height of the [MMScaffold] top bar (status bar included) that scrolling content has to start
 * below. 0.dp outside of an [MMScaffold].
 */
val LocalMMTopBarPadding = staticCompositionLocalOf { 0.dp }

/**
 * `contentPadding` for a screen's main scrolling container: starts below the glass top bar and
 * ends above the floating navigation bar, while still scrolling underneath both.
 */
@Composable
fun mmScrollContentPadding(): PaddingValues = PaddingValues(
    top = LocalMMTopBarPadding.current,
    bottom = LocalMMFloatingBarPadding.current,
)

/**
 * The scaffold [PaddingValues] minus the top edge, for the container around a scroller that
 * handles the top itself via [mmScrollContentPadding].
 */
@Composable
fun PaddingValues.withoutTop(): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(layoutDirection),
        end = calculateEndPadding(layoutDirection),
        bottom = calculateBottomPadding(),
    )
}

/**
 * Drop-in replacement for Material 3 [Scaffold] whose top bar floats over the content as liquid
 * glass. Scaffold already lays the body out full-size under the top bar and only reports the bar
 * height through `PaddingValues`, so content that applies just the *bottom* padding and moves the
 * top one into its scroll `contentPadding` scrolls underneath the glass.
 */
@Composable
fun MMScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    content: @Composable (PaddingValues) -> Unit,
) {
    val backdrop = rememberGlassBackdrop()
    Scaffold(
        modifier = modifier,
        topBar = { CompositionLocalProvider(LocalMMBackdrop provides backdrop) { topBar() } },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop),
        ) {
            CompositionLocalProvider(LocalMMTopBarPadding provides padding.calculateTopPadding()) {
                content(padding)
            }
        }
    }
}

/**
 * A layer backdrop for glass to sample. Most screens sit on a transparent container over
 * MetanMobileBackground (outside the layer), so the theme background is painted first - otherwise
 * the lens would refract see-through pixels.
 */
@Composable
internal fun rememberGlassBackdrop(): com.kyant.backdrop.backdrops.LayerBackdrop {
    val backgroundColor = MaterialTheme.colorScheme.background
    return rememberLayerBackdrop {
        drawRect(backgroundColor)
        drawContent()
    }
}

/** Tint laid over the sampled backdrop; nearly opaque where blur isn't available (Android < 12). */
@Composable
internal fun glassTint(): Color = MMNavigationDefaults.navigationContainerColor()
    .copy(alpha = if (isRenderEffectSupported()) 0.5f else 0.94f)

/**
 * Liquid glass surface: [backdrop] blurred, saturated and refracted towards the rounded edges,
 * under a [tint] with a specular rim highlight. Without a backdrop (e.g. over a plain sheet) only
 * the tint, rim highlight and shadow are drawn.
 */
internal fun Modifier.glass(
    backdrop: Backdrop?,
    shape: CornerBasedShape,
    tint: Color,
): Modifier = this.drawBackdrop(
    backdrop = backdrop ?: emptyBackdrop(),
    shape = { shape },
    effects = {
        if (backdrop != null) {
            vibrancy()
            blur(4.dp.toPx())
            lens(
                refractionHeight = 12.dp.toPx(),
                refractionAmount = 24.dp.toPx(),
                depthEffect = true,
            )
        }
    },
    shadow = { Shadow(radius = 16.dp, color = Color.Black.copy(alpha = 0.12f)) },
    onDrawSurface = { drawRect(if (backdrop != null) tint else tint.copy(alpha = 0.85f)) },
)

/** Round liquid-glass icon button. */
@Composable
fun MMGlassIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    contentColor: Color = MMColors.toolbarIconColor,
) {
    Box(
        modifier = modifier
            .size(size)
            .glass(LocalMMBackdrop.current, CircleShape, glassTint())
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(iconSize),
        )
    }
}

/**
 * Capsule liquid-glass button with an optional leading icon. [tint] colours the glass itself -
 * e.g. the brand blue for a primary action - defaulting to the neutral glass tint.
 */
@Composable
fun MMGlassButton(
    textRes: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    contentColor: Color = MMColors.toolbarIconColor,
    tint: Color = glassTint(),
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .glass(LocalMMBackdrop.current, CircleShape, tint)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(18.dp),
            )
        }
        Text(
            text = stringResource(textRes),
            style = MMTypography.bodyMedium,
            color = contentColor,
        )
    }
}
