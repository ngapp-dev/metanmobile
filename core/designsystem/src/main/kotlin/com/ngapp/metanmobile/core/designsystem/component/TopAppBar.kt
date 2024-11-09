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

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.ngapp.metanmobile.core.designsystem.component

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.R
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.toolbarIconColor

@Composable
fun MMToolbarWithNavIcon(
    @StringRes titleResId: Int? = null,
    onNavigationClick: () -> Unit,
) {
    TopAppBar(
        title = {
            if (titleResId != null) {
                Text(
                    stringResource(titleResId),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    style = MMTypography.displayMedium
                )
            }
        },
        navigationIcon = {
            MMToolbarAction(
                icon = MMIcons.ArrowBackFilled,
                contentDescription = stringResource(R.string.core_designsystem_description_nav_icon),
                onClick = onNavigationClick
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(MMColors.secondary),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun MMFilterSearchButtonsTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: TopAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(MMColors.secondary),
    onSearchActionClick: () -> Unit = {},
    onFilterActionClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            if (title.isNotEmpty())
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    style = MMTypography.displayLarge
                )
        },
        actions = {
            MMToolbarAction(
                icon = MMIcons.Search,
                contentDescription = stringResource(R.string.core_designsystem_description_search_icon),
                onClick = onSearchActionClick
            )
            MMToolbarAction(
                icon = MMIcons.FilterListOutlined,
                contentDescription = stringResource(R.string.core_designsystem_description_filter_icon),
                onClick = onFilterActionClick
            )
        },
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .testTag("metanMobileTopAppBar"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MMNavShareButtonsTopAppBar(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int? = null,
    colors: TopAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(MMColors.secondary),
    onNavigationClick: () -> Unit = {},
    onShareActionClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            if (titleRes != null)
                Text(
                    text = stringResource(id = titleRes),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    style = MMTypography.displayLarge
                )
        },
        navigationIcon = {
            MMToolbarAction(
                icon = MMIcons.ArrowBackFilled,
                contentDescription = stringResource(R.string.core_designsystem_description_nav_icon),
                onClick = onNavigationClick
            )
        },
        actions = {
            MMToolbarAction(
                icon = MMIcons.ShareFilled,
                contentDescription = stringResource(R.string.core_designsystem_description_share_icon),
                onClick = onShareActionClick
            )
        },
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .testTag("metanMobileTopAppBar"),
    )
}

@Composable
fun MMHomeTopAppBar(
    onUserClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onMenuClicked: () -> Unit,
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Image(
                        imageVector = ImageVector.vectorResource(MMIcons.LogoFullSolid),
                        contentDescription = stringResource(R.string.core_designsystem_description_toolbar_title_img),
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                } else {
                    Image(
                        painter = painterResource(MMIcons.LogoFullSolidPng),
                        contentDescription = stringResource(R.string.core_designsystem_description_toolbar_title_img),
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }
        },
        actions = {
            MMToolbarAction(
                icon = MMIcons.PersonFilled,
                contentDescription = stringResource(R.string.core_designsystem_description_cabinet_icon),
                onClick = onUserClicked
            )
            MMToolbarAction(
                icon = MMIcons.EditFilled,
                contentDescription = stringResource(R.string.core_designsystem_description_edit_icon),
                onClick = onEditClicked
            )
            MMToolbarAction(
                icon = MMIcons.MenuFilled,
                contentDescription = stringResource(R.string.core_designsystem_description_menu_icon),
                onClick = onMenuClicked
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(MMColors.secondary),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun MMCabinetTopAppBar(
    @StringRes titleResId: Int? = null,
    onNavigationClick: () -> Unit,
    onOpenInBrowserClicked: () -> Unit,
    onGetAccessClicked: () -> Unit,
) {
    TopAppBar(
        title = {
            if (titleResId != null) {
                Text(
                    stringResource(titleResId),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    style = MMTypography.displayMedium
                )
            }
        },
        navigationIcon = {
            MMToolbarAction(
                icon = MMIcons.ArrowBackFilled,
                contentDescription = stringResource(R.string.core_designsystem_description_nav_icon),
                onClick = onNavigationClick
            )
        },
        actions = {
            CabinetMoreAction(
                onOpenInBrowserClicked = onOpenInBrowserClicked,
                onGetAccessClicked = onGetAccessClicked
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(MMColors.secondary),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun MMMenuTopAppBar(
    @StringRes titleResId: Int? = null,
    onNavigationClick: () -> Unit,
    onSupportClick: () -> Unit,
) {
    TopAppBar(
        title = {
            if (titleResId != null) {
                Text(
                    stringResource(titleResId),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    style = MMTypography.displayMedium
                )
            }
        },
        navigationIcon = {
            MMToolbarAction(
                icon = MMIcons.ArrowBackFilled,
                contentDescription = stringResource(R.string.core_designsystem_description_nav_icon),
                onClick = onNavigationClick
            )
        },
        actions = {
            MMToolbarAction(
                icon = MMIcons.PhoneFilled,
                contentDescription = stringResource(R.string.core_designsystem_description_support_icon),
                onClick = onSupportClick
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(MMColors.secondary),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun MMOnboardingTopAppBar(
    shouldShowNavigationButton: Boolean,
    onNavigationClick: () -> Unit,
    onSkipActionClick: () -> Unit,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            if (shouldShowNavigationButton) {
                MMToolbarAction(
                    icon = MMIcons.ArrowBackFilled,
                    contentDescription = stringResource(R.string.core_designsystem_description_nav_icon),
                    onClick = onNavigationClick
                )
            }
        },
        actions = {
            TextButton(onClick = onSkipActionClick) {
                Text(
                    text = stringResource(R.string.core_designsystem_onboarding_button_skip),
                    color = MMColors.toolbarIconColor,
                    style = MMTypography.titleLarge
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(Color.Transparent),
        modifier = Modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MMFilterSearchFieldTopAppBar(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int? = null,
    @StringRes placeholderRes: Int? = null,
    searchText: String,
    onSearchTextChanged: (String) -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(MMColors.secondary),
    onNavigationClick: () -> Unit = {},
    onFilterActionClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            if (titleRes != null)
                Text(
                    text = stringResource(id = titleRes),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    style = MMTypography.displayLarge
                )
        },
        navigationIcon = {
            MMToolbarAction(
                icon = MMIcons.ArrowBackFilled,
                contentDescription = stringResource(R.string.core_designsystem_description_nav_icon),
                onClick = onNavigationClick
            )
        },
        actions = {
            SearchField(
                modifier = Modifier
                    .padding(start = 52.dp)
                    .weight(1f),
                placeholderRes = placeholderRes,
                searchText = searchText,
                onSearchTextChanged = onSearchTextChanged,
                onDoneClick = onDoneClick,
                onClearClick = onClearClick
            )
            MMToolbarAction(
                icon = MMIcons.FilterListOutlined,
                contentDescription = stringResource(R.string.core_designsystem_description_filter_icon),
                onClick = onFilterActionClick
            )
        },
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .testTag("metanMobileTopAppBar"),
    )
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    @StringRes placeholderRes: Int? = null,
    searchText: String,
    onSearchTextChanged: (String) -> Unit = {},
    onDoneClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    TextField(
        modifier = modifier
            .onFocusChanged { focusState -> showClearButton = (focusState.isFocused) }
            .focusRequester(focusRequester),
        value = searchText,
        onValueChange = onSearchTextChanged,
        placeholder = {
            if (placeholderRes != null) {
                Text(
                    text = stringResource(id = placeholderRes),
                    color = Gray400
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = Blue,
            focusedIndicatorColor = Blue,
            unfocusedIndicatorColor = Gray400,
        ),
        trailingIcon = {
            AnimatedVisibility(
                visible = showClearButton,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MMToolbarAction(
                    icon = MMIcons.Close,
                    contentDescription = stringResource(R.string.core_designsystem_description_clear_search_icon),
                    tint = MMColors.toolbarIconColor,
                    onClick = onClearClick
                )
//                IconButton(onClick = onClearClick) {
//                    Icon(
//                        imageVector = MMIcons.Close,
//                        contentDescription = stringResource(R.string.core_designsystem_description_clear_search_icon),
//                        tint = MMColors.toolbarIconColor
//                    )
//                }
            }
        },
        maxLines = 1,
        singleLine = true,
        textStyle = MMTypography.bodyLarge,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onDoneClick()
            keyboardController?.hide()
        }),
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
            contentDescription = stringResource(R.string.core_designsystem_description_toolbar_more_icon),
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
                        text = stringResource(R.string.core_designsystem_button_open_in_browser),
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
                        text = stringResource(R.string.core_designsystem_button_how_to_get_access),
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
    tint: Color = MMColors.toolbarIconColor,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("MMFilterSearchButtonsTopAppBar")
@Composable
private fun MMFilterSearchButtonsTopAppBarPreview() {
    MMTheme {
        MMFilterSearchButtonsTopAppBar(title = stringResource(id = android.R.string.untitled))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("MMFilterSearchFieldTopAppBar")
@Composable
private fun MMFilterSearchFieldTopAppBarPreview() {
    MMTheme {
        MMFilterSearchFieldTopAppBar(
            titleRes = android.R.string.untitled,
            placeholderRes = android.R.string.untitled,
            searchText = "Search"
        )
    }
}