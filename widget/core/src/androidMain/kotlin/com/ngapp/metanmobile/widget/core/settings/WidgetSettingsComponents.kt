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

package com.ngapp.metanmobile.widget.core.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMButton
import com.ngapp.metanmobile.core.designsystem.theme.Black
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.widget.core.appearance.WIDGET_PERCENT_MAX
import com.ngapp.metanmobile.widget.core.appearance.WidgetAppearance
import com.ngapp.metanmobile.widget.core.appearance.WidgetColor
import com.ngapp.metanmobile.widget.core.appearance.WidgetSurfaceStyle
import com.ngapp.metanmobile.widget.core.appearance.WidgetThemeMode
import com.ngapp.metanmobile.widget.core.theme.WidgetDimens
import com.ngapp.metanmobile.widget.core.theme.WidgetPalette
import com.ngapp.metanmobile.widget.core.theme.WidgetStatus
import com.ngapp.metanmobile.widget.core.theme.isNight
import com.ngapp.metanmobile.widget.core.theme.palette
import com.ngapp.metanmobile.widget.core.theme.statusDotRing
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlin.math.roundToInt

// Building blocks every widget's settings screen is made of, so they all look the same: the
// live preview on top of the real wallpaper, and a panel of rows (switches, color swatches,
// sliders) under it.

/**
 * The whole settings screen. The window shows the user's wallpaper (see the activity theme),
 * so [preview] is seen exactly where the widget will be: on top of it. The preview stays put
 * while the settings panel under it scrolls.
 */
@Composable
fun WidgetSettingsLayout(
    onSave: () -> Unit,
    preview: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Pinned: only the panel below scrolls, so every change stays visible in the preview.
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .heightIn(min = 200.dp)
                .padding(16.dp),
        ) {
            preview()
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            content()
            Spacer(Modifier.height(16.dp))
            MMButton(
                buttonText = SharedRes.strings.widget_settings_button_save,
                buttonBackgroundColor = Blue,
                fontColor = White,
                borderStrokeColor = Blue,
                onClick = { onSave() },
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

/** The theme, background and tile settings shared by every widget. */
@Composable
fun WidgetAppearanceSettings(
    appearance: WidgetAppearance,
    onAppearanceChange: (WidgetAppearance) -> Unit,
) {
    WidgetChoiceRow(
        title = SharedRes.strings.widget_settings_text_theme,
        options = WidgetThemeMode.entries,
        selected = appearance.themeMode,
        optionTitle = { it.title },
        onSelect = { onAppearanceChange(appearance.copy(themeMode = it)) },
    )
    WidgetSettingsSection(SharedRes.strings.widget_settings_text_background)
    WidgetSurfaceSettings(
        style = appearance.background,
        onStyleChange = { onAppearanceChange(appearance.copy(background = it)) },
    )
    WidgetSettingsSection(SharedRes.strings.widget_settings_text_tiles)
    WidgetSurfaceSettings(
        style = appearance.tiles,
        onStyleChange = { onAppearanceChange(appearance.copy(tiles = it)) },
    )
}

/** Color and opacity, or — with liquid glass on — the glass tint and its strength instead. */
@Composable
private fun WidgetSurfaceSettings(
    style: WidgetSurfaceStyle,
    onStyleChange: (WidgetSurfaceStyle) -> Unit,
) {
    val glass = style.glass
    WidgetSwitchRow(
        title = SharedRes.strings.widget_settings_text_glass,
        checked = glass.enabled,
        onCheckedChange = { onStyleChange(style.copy(glass = glass.copy(enabled = it))) },
    )
    if (glass.enabled) {
        WidgetColorRow(
            title = SharedRes.strings.widget_settings_text_glass_tint,
            selected = glass.tint,
            onSelect = { onStyleChange(style.copy(glass = glass.copy(tint = it))) },
        )
        WidgetSliderRow(
            title = SharedRes.strings.widget_settings_text_glass_intensity,
            value = glass.tintStrength,
            onValueChange = { onStyleChange(style.copy(glass = glass.copy(tintStrength = it))) },
        )
    } else {
        WidgetColorRow(
            title = SharedRes.strings.widget_settings_text_color,
            selected = style.color,
            onSelect = { onStyleChange(style.copy(color = it)) },
        )
        WidgetSliderRow(
            title = SharedRes.strings.widget_settings_text_opacity,
            value = style.opacity,
            onValueChange = { onStyleChange(style.copy(opacity = it)) },
        )
    }
}

private val InactiveTrack = Gray400.copy(alpha = 0.4f)

@Composable
fun WidgetSettingsSection(title: StringResource) {
    Text(
        text = stringResource(title),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 20.dp, bottom = 4.dp),
    )
}

@Composable
fun WidgetSwitchRow(
    title: StringResource,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    SettingsRow(
        title = title,
        modifier = Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Switch,
            onValueChange = onCheckedChange,
        ),
    ) {
        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            // Explicit: MMTheme's color roles are tuned for the app's own screens and leave
            // these controls brown/blue in places.
            colors = SwitchDefaults.colors(
                checkedTrackColor = Blue,
                checkedThumbColor = White,
                uncheckedTrackColor = InactiveTrack,
                uncheckedThumbColor = White,
                uncheckedBorderColor = Color.Transparent,
                // A switch locked on (e.g. the last tile left) must still read as "on".
                disabledCheckedTrackColor = Blue.copy(alpha = 0.5f),
                disabledCheckedThumbColor = White,
                disabledUncheckedTrackColor = InactiveTrack,
                disabledUncheckedThumbColor = White.copy(alpha = 0.6f),
                disabledUncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

/** A title over a row of pill-shaped choices, the selected one filled blue. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> WidgetChoiceRow(
    title: StringResource,
    options: List<T>,
    selected: T,
    optionTitle: (T) -> StringResource,
    onSelect: (T) -> Unit,
) {
    SettingsRow(title = title) {}
    // Wraps onto a second line when the labels don't fit (long translations, narrow phones).
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            val shape = RoundedCornerShape(50)
            Text(
                text = stringResource(optionTitle(option)),
                style = MaterialTheme.typography.headlineMedium,
                color = if (isSelected) White else MaterialTheme.typography.headlineMedium.color,
                modifier = Modifier
                    .clip(shape)
                    .background(if (isSelected) Blue else Color.Transparent)
                    .border(1.dp, if (isSelected) Blue else Gray400, shape)
                    .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(option) })
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

/** A short explanation under a setting. */
@Composable
fun WidgetSettingsHint(text: StringResource) {
    Text(
        text = stringResource(text),
        style = MaterialTheme.typography.headlineMedium,
        color = Gray400,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
fun WidgetColorRow(
    title: StringResource,
    selected: WidgetColor,
    onSelect: (WidgetColor) -> Unit,
) {
    SettingsRow(title = title) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WidgetColor.entries.forEach { color ->
                ColorSwatch(color = color, selected = color == selected, onClick = { onSelect(color) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSliderRow(
    title: StringResource,
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = SliderDefaults.colors(
        thumbColor = Blue,
        activeTrackColor = Blue,
        inactiveTrackColor = InactiveTrack,
    )
    Column {
        SettingsRow(title = title) {
            Text(text = value.toString(), style = MaterialTheme.typography.headlineMedium)
        }
        // A thin track and a small round thumb instead of Material's thick default.
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = 0f..WIDGET_PERCENT_MAX.toFloat(),
            interactionSource = interactionSource,
            colors = colors,
            thumb = {
                Box(
                    Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Blue),
                )
            },
            track = { state ->
                SliderDefaults.Track(
                    sliderState = state,
                    colors = colors,
                    modifier = Modifier.height(4.dp),
                    thumbTrackGapSize = 0.dp,
                    drawStopIndicator = null,
                )
            },
        )
    }
}

@Composable
private fun SettingsRow(
    title: StringResource,
    modifier: Modifier = Modifier,
    control: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f),
        )
        control()
    }
}

/** A square of the color, ringed in blue when selected; AUTO is split light/dark. */
@Composable
private fun ColorSwatch(color: WidgetColor, selected: Boolean, onClick: () -> Unit) {
    val description = stringResource(color.title)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .border(2.dp, if (selected) Blue else Color.Transparent, RoundedCornerShape(6.dp))
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .semantics { contentDescription = description },
    ) {
        val shape = RoundedCornerShape(3.dp)
        Canvas(
            modifier = Modifier
                .size(30.dp)
                .clip(shape)
                .border(1.dp, Gray400, shape),
        ) {
            when (color) {
                WidgetColor.AUTO -> {
                    drawRect(White)
                    drawPath(
                        Path().apply {
                            moveTo(size.width, 0f)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        },
                        Black,
                    )
                }
                WidgetColor.BLACK -> drawRect(Black)
                WidgetColor.WHITE -> drawRect(White)
                WidgetColor.BLUE -> drawRect(Blue)
            }
        }
    }
}

/** Colors of the widget preview, resolved for the current system theme. */
class WidgetPreviewColors(
    val tile: Color,
    val isTileGlass: Boolean,
    val onTile: Color,
    internal val palette: WidgetPalette,
    internal val isNight: Boolean,
)

// Compose version of res/drawable/widget_glass_*.xml, so the preview shows the same glass.
private val GlassHighlight = Brush.verticalGradient(
    0f to Color.White.copy(alpha = 0.28f),
    0.5f to Color.White.copy(alpha = 0.06f),
    1f to Color.Transparent,
)

private fun Modifier.glass(isGlass: Boolean, shape: Shape): Modifier =
    if (!isGlass) this else background(GlassHighlight, shape).border(1.dp, Color.White.copy(alpha = 0.4f), shape)

/**
 * The widget as it will look, drawn with Compose from the same palette Glance uses; the
 * widget's own tiles go in [content]. [halfWidth] mirrors WidgetContainer's.
 */
@Composable
fun WidgetPreview(
    appearance: WidgetAppearance,
    halfWidth: Boolean = false,
    content: @Composable RowScope.(WidgetPreviewColors) -> Unit,
) {
    val isNight = appearance.themeMode.isNight(isSystemNight = isSystemInDarkTheme())
    val palette = appearance.palette()
    val colors = WidgetPreviewColors(
        tile = palette.tile.fill.resolve(isNight),
        isTileGlass = palette.tile.isGlass,
        onTile = palette.onTile.resolve(isNight),
        palette = palette,
        isNight = isNight,
    )
    val shape = RoundedCornerShape(WidgetDimens.containerCornerRadius)
    Box(Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(WidgetDimens.tileSpacing),
            modifier = Modifier
                .fillMaxWidth(if (halfWidth) 0.5f else 1f)
                .clip(shape)
                .background(palette.container.fill.resolve(isNight))
                .glass(palette.container.isGlass, shape)
                .padding(WidgetDimens.containerPadding),
        ) {
            content(colors)
        }
    }
}

@Composable
fun RowScope.WidgetPreviewTile(colors: WidgetPreviewColors, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(WidgetDimens.tileCornerRadius)
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(shape)
            .background(colors.tile)
            .glass(colors.isTileGlass, shape)
            .padding(
                horizontal = WidgetDimens.tilePaddingHorizontal,
                vertical = WidgetDimens.tilePaddingVertical,
            ),
    ) {
        content()
    }
}

/** Same sizes as WidgetTextStyles.value / caption on the real widget. */
@Composable
fun WidgetPreviewValue(text: String, colors: WidgetPreviewColors, trailing: @Composable () -> Unit = {}) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            color = colors.onTile,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        trailing()
    }
}

@Composable
fun WidgetPreviewCaption(text: String, colors: WidgetPreviewColors) {
    Text(
        text = text,
        color = colors.onTile,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

/** Mirrors WidgetStatusDot on the real widget, ring included. */
@Composable
fun WidgetPreviewStatusDot(status: WidgetStatus, colors: WidgetPreviewColors) {
    val ring = colors.palette.statusDotRing(status)?.resolve(colors.isNight)
    val ringWidth = if (ring != null) WidgetDimens.statusDotRingWidth else 0.dp
    Spacer(Modifier.width(6.dp - ringWidth))
    Box(
        modifier = Modifier
            .size(WidgetDimens.statusDotSize + ringWidth * 2)
            .clip(CircleShape)
            .background(ring ?: Color.Transparent)
            .padding(ringWidth)
            .clip(CircleShape)
            .background(status.color),
    )
}

private val WidgetThemeMode.title: StringResource
    get() = when (this) {
        WidgetThemeMode.SYSTEM -> SharedRes.strings.widget_settings_theme_system
        WidgetThemeMode.LIGHT -> SharedRes.strings.widget_settings_theme_light
        WidgetThemeMode.DARK -> SharedRes.strings.widget_settings_theme_dark
    }

private val WidgetColor.title: StringResource
    get() = when (this) {
        WidgetColor.BLACK -> SharedRes.strings.widget_settings_color_black
        WidgetColor.WHITE -> SharedRes.strings.widget_settings_color_white
        WidgetColor.BLUE -> SharedRes.strings.widget_settings_color_blue
        WidgetColor.AUTO -> SharedRes.strings.widget_settings_color_auto
    }
