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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.ngapp.metanmobile.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.toolbarIconColor
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

private val GlassControlSize = 48.dp

@Composable
fun MMToolbarWithNavIcon(
    titleRes: StringResource? = null,
    onNavigationClick: () -> Unit,
) {
    MMGlassTopAppBar(
        navigationIcon = { NavigationBackAction(onNavigationClick) },
        title = titleRes?.let { { ToolbarTitle(stringResource(it)) } },
    )
}

@Composable
fun MMFilterSearchButtonsTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    onSearchActionClick: () -> Unit = {},
    onFilterActionClick: () -> Unit = {},
) {
    MMGlassTopAppBar(
        modifier = modifier.testTag("metanMobileTopAppBar"),
        title = if (title.isNotEmpty()) ({ ToolbarTitle(title) }) else null,
        actions = {
            MMToolbarAction(
                icon = MMIcons.Search,
                contentDescription = stringResource(SharedRes.strings.designsystem_description_search_icon),
                onClick = onSearchActionClick
            )
            MMToolbarAction(
                icon = MMIcons.FilterListOutlined,
                contentDescription = stringResource(SharedRes.strings.designsystem_description_filter_icon),
                onClick = onFilterActionClick
            )
        },
    )
}

@Composable
fun MMNavShareButtonsTopAppBar(
    modifier: Modifier = Modifier,
    titleRes: StringResource? = null,
    onNavigationClick: () -> Unit = {},
    onShareActionClick: () -> Unit = {},
    showScrim: Boolean = true,
) {
    MMGlassTopAppBar(
        modifier = modifier.testTag("metanMobileTopAppBar"),
        showScrim = showScrim,
        navigationIcon = { NavigationBackAction(onNavigationClick) },
        title = titleRes?.let { { ToolbarTitle(stringResource(it)) } },
        actions = {
            MMToolbarAction(
                icon = MMIcons.Share,
                contentDescription = stringResource(SharedRes.strings.designsystem_description_share_icon),
                onClick = onShareActionClick
            )
        },
    )
}

@Composable
fun MMHomeTopAppBar(
    onEditClicked: () -> Unit,
    onMenuClicked: () -> Unit,
) {
    MMGlassTopAppBar(
        title = {
            Image(
                painter = painterResource(MMIcons.LogoFullSolid),
                contentDescription = stringResource(SharedRes.strings.designsystem_description_toolbar_title_img),
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .height(22.dp)
                    // Master's logo asset is a vector at 274.75dp x 36.24dp (aspect ~7.58:1).
                    // Our moko PNG is only shipped as a single "@2x" density variant, which
                    // gets bucketed as xhdpi — depending on how the KMP resource loader
                    // reports intrinsic size for that bucket, relying on it alone can size
                    // the logo wrong. Pin the real aspect ratio explicitly so it always
                    // renders at a fixed, correct size regardless of that.
                    .aspectRatio(274.75f / 36.24f)
            )
        },
        actions = {
            MMToolbarAction(
                icon = MMIcons.EditFilled,
                contentDescription = stringResource(SharedRes.strings.designsystem_description_edit_icon),
                onClick = onEditClicked
            )
            MMToolbarAction(
                icon = MMIcons.MenuFilled,
                contentDescription = stringResource(SharedRes.strings.designsystem_description_menu_icon),
                onClick = onMenuClicked
            )
        },
    )
}

@Composable
fun MMCabinetTopAppBar(
    titleRes: StringResource? = null,
    onNavigationClick: () -> Unit,
    onOpenInBrowserClicked: () -> Unit,
    onGetAccessClicked: () -> Unit,
) {
    MMGlassTopAppBar(
        navigationIcon = { NavigationBackAction(onNavigationClick) },
        title = titleRes?.let { { ToolbarTitle(stringResource(it)) } },
        actions = {
            CabinetMoreAction(
                onOpenInBrowserClicked = onOpenInBrowserClicked,
                onGetAccessClicked = onGetAccessClicked
            )
        },
    )
}

@Composable
fun MMMenuTopAppBar(
    titleRes: StringResource? = null,
    onNavigationClick: () -> Unit,
    onSupportClick: () -> Unit,
) {
    MMGlassTopAppBar(
        navigationIcon = { NavigationBackAction(onNavigationClick) },
        title = titleRes?.let { { ToolbarTitle(stringResource(it)) } },
        actions = {
            MMToolbarAction(
                icon = MMIcons.PhoneFilled,
                contentDescription = stringResource(SharedRes.strings.designsystem_description_support_icon),
                onClick = onSupportClick
            )
        },
    )
}

@Composable
fun MMOnboardingTopAppBar(
    shouldShowNavigationButton: Boolean,
    onNavigationClick: () -> Unit,
    onSkipActionClick: () -> Unit,
) {
    MMGlassTopAppBar(
        showScrim = false,
        navigationIcon = if (shouldShowNavigationButton) {
            {
                NavigationBackAction(
                    onClick = onNavigationClick,
                    modifier = Modifier.semantics { contentDescription = "Back" },
                )
            }
        } else {
            null
        },
        actions = {
            TextButton(onClick = onSkipActionClick) {
                Text(
                    text = stringResource(SharedRes.strings.designsystem_onboarding_skip),
                    color = MMColors.toolbarIconColor,
                    style = MMTypography.titleLarge
                )
            }
        },
    )
}

@Composable
fun MMFilterSearchFieldTopAppBar(
    modifier: Modifier = Modifier,
    titleRes: StringResource? = null,
    placeholderRes: StringResource? = null,
    searchText: String,
    onSearchTextChanged: (String) -> Unit = {},
    onNavigationClick: () -> Unit = {},
    onFilterActionClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
) {
    // The search field takes the title capsule's place; the title itself isn't shown while
    // searching (as before, where the field covered it).
    MMGlassTopAppBar(
        modifier = modifier.testTag("metanMobileTopAppBar"),
        navigationIcon = { NavigationBackAction(onNavigationClick) },
        title = {
            SearchField(
                modifier = Modifier.fillMaxWidth(),
                placeholderRes = placeholderRes ?: titleRes,
                searchText = searchText,
                onSearchTextChanged = onSearchTextChanged,
                onDoneClick = onDoneClick,
                onClearClick = onClearClick
            )
        },
        titleInCapsule = true,
        actions = {
            MMToolbarAction(
                icon = MMIcons.FilterListOutlined,
                contentDescription = stringResource(SharedRes.strings.designsystem_description_filter_icon),
                onClick = onFilterActionClick
            )
        },
    )
}

/**
 * Telegram-style top bar: liquid-glass controls floating over the content - a round navigation
 * button and an actions capsule - with the title start-aligned straight over the content (no glass
 * behind it). Glass samples [LocalMMBackdrop], so the bar belongs in an [MMScaffold]'s
 * `topBar`.
 *
 * @param titleInCapsule puts the title in a glass capsule filling the space between the controls
 * - for inputs like the search field.
 * @param showScrim fades the content that scrolls under the bar so the title and system icons
 * stay legible; off for screens whose header image deliberately runs up under the bar.
 */
@Composable
internal fun MMGlassTopAppBar(
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    titleInCapsule: Boolean = false,
    actions: (@Composable RowScope.() -> Unit)? = null,
    showScrim: Boolean = true,
) {
    val backdrop = LocalMMBackdrop.current
    val tint = glassTint()
    val scrimColor = MaterialTheme.colorScheme.background
    Box(modifier = modifier.fillMaxWidth()) {
        if (showScrim) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0f to scrimColor.copy(alpha = 0.95f),
                            0.6f to scrimColor.copy(alpha = 0.8f),
                            1f to scrimColor.copy(alpha = 0f),
                        ),
                    ),
            )
        }
        CompositionLocalProvider(LocalContentColor provides MMColors.toolbarIconColor) {
            Layout(
                contents = listOf(
                    {
                        if (navigationIcon != null) {
                            Box(
                                modifier = Modifier
                                    .size(GlassControlSize)
                                    .glass(backdrop, CircleShape, tint),
                                contentAlignment = Alignment.Center,
                            ) {
                                navigationIcon()
                            }
                        }
                    },
                    {
                        if (title != null) {
                            Box(
                                modifier = if (titleInCapsule) {
                                    Modifier
                                        .height(GlassControlSize)
                                        .glass(backdrop, CircleShape, tint)
                                        .padding(horizontal = 18.dp)
                                } else {
                                    Modifier
                                },
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                // Same style as the section headers inside screens ("All news").
                                ProvideTextStyle(MMTypography.displayLarge) { title() }
                            }
                        }
                    },
                    {
                        if (actions != null) {
                            Row(
                                modifier = Modifier
                                    .height(GlassControlSize)
                                    .glass(backdrop, CircleShape, tint),
                                verticalAlignment = Alignment.CenterVertically,
                                content = actions,
                            )
                        }
                    },
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) { (navMeasurables, titleMeasurables, actionMeasurables), constraints ->
                val gap = 8.dp.roundToPx()
                val width = constraints.maxWidth
                val loose = constraints.copy(minWidth = 0, minHeight = 0)
                val nav = navMeasurables.firstOrNull()?.measure(loose)
                val action = actionMeasurables.firstOrNull()?.measure(loose)
                val navWidth = nav?.let { it.width + gap } ?: 0
                val actionWidth = action?.let { it.width + gap } ?: 0
                val titleMaxWidth = (width - navWidth - actionWidth).coerceAtLeast(0)
                val title = titleMeasurables.firstOrNull()?.measure(
                    loose.copy(
                        minWidth = if (titleInCapsule) titleMaxWidth else 0,
                        maxWidth = titleMaxWidth,
                    ),
                )
                val height = maxOf(
                    GlassControlSize.roundToPx(),
                    nav?.height ?: 0,
                    action?.height ?: 0,
                    title?.height ?: 0,
                )
                layout(width, height) {
                    nav?.placeRelative(0, (height - nav.height) / 2)
                    action?.placeRelative(width - action.width, (height - action.height) / 2)
                    title?.placeRelative(
                        // Plain titles line up with the screen content's 16dp inset.
                        x = if (titleInCapsule || nav != null) navWidth else 4.dp.roundToPx(),
                        y = (height - title.height) / 2,
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolbarTitle(text: String) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun NavigationBackAction(onClick: () -> Unit, modifier: Modifier = Modifier) {
    MMToolbarAction(
        icon = MMIcons.ArrowBackFilled,
        contentDescription = stringResource(SharedRes.strings.designsystem_description_nav_icon),
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    placeholderRes: StringResource? = null,
    searchText: String,
    onSearchTextChanged: (String) -> Unit = {},
    onDoneClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    // BasicTextField rather than TextField: the capsule is the field's container now, and
    // TextField's 56dp minimum height and underline don't fit inside it.
    BasicTextField(
        modifier = modifier
            .onFocusChanged { focusState -> showClearButton = (focusState.isFocused) }
            .focusRequester(focusRequester),
        value = searchText,
        onValueChange = onSearchTextChanged,
        singleLine = true,
        // Typography already carries the theme's text color; colorScheme.onBackground is the
        // card/background color in this palette (see Theme.kt), so text drawn with it vanished.
        textStyle = MMTypography.bodyLarge,
        cursorBrush = SolidColor(Blue),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onDoneClick()
            keyboardController?.hide()
        }),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    if (searchText.isEmpty() && placeholderRes != null) {
                        Text(
                            text = stringResource(placeholderRes),
                            style = MMTypography.bodyLarge,
                            color = Gray400,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    innerTextField()
                }
                AnimatedVisibility(
                    visible = showClearButton,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    MMToolbarAction(
                        icon = MMIcons.Close,
                        contentDescription = stringResource(SharedRes.strings.designsystem_description_clear_search_icon),
                        tint = MMColors.toolbarIconColor,
                        onClick = onClearClick,
                        modifier = Modifier.size(36.dp),
                    )
                }
            }
        },
    )
}

@Composable
private fun CabinetMoreAction(
    onOpenInBrowserClicked: () -> Unit,
    onGetAccessClicked: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = MMIcons.MoreVert,
            contentDescription = stringResource(SharedRes.strings.designsystem_description_toolbar_more_icon),
            tint = MMColors.toolbarIconColor
        )
        DropdownMenu(
            expanded = expanded,
            containerColor = MaterialTheme.colorScheme.onSurface,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onOpenInBrowserClicked()
                },
                text = {
                    Text(
                        text = stringResource(SharedRes.strings.designsystem_button_open_in_browser),
                        style = MMTypography.bodyLarge,
                    )
                }
            )
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onGetAccessClicked()
                },
                text = {
                    Text(
                        text = stringResource(SharedRes.strings.designsystem_button_how_to_get_access),
                        style = MMTypography.bodyLarge,
                    )
                }
            )
        }
    }
}

@Composable
private fun MMToolbarAction(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = MMColors.toolbarIconColor,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}
