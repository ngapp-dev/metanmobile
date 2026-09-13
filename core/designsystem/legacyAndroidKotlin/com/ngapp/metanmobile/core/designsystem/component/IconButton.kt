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

package com.ngapp.metanmobile.core.designsystem.component

import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme

/**
 * MetanMobile toggle button with icon and checked icon content slots. Wraps Material 3
 * [IconButton].
 *
 * @param checked Whether the toggle button is currently checked.
 * @param onCheckedChange Called when the user clicks the toggle button and toggles checked.
 * @param modifier Modifier to be applied to the toggle button.
 * @param enabled Controls the enabled state of the toggle button. When `false`, this toggle button
 * will not be clickable and will appear disabled to accessibility services.
 * @param icon The icon content to show when unchecked.
 * @param checkedIcon The icon content to show when checked.
 */
@Composable
fun MMIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit,
    checkedIcon: @Composable () -> Unit = icon,
) {
    // TODO: File bug
    // Can't use regular IconToggleButton as it doesn't include a shape (appears square)
    FilledIconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = IconButtonDefaults.iconToggleButtonColors(
            containerColor = Color.Transparent
        ),
        enabled = enabled,
    ) {
        if (checked) checkedIcon() else icon()
    }
}

/**
 * MetanMobile filled icon button. Wraps Material 3's [FilledIconButton].
 *
 * @param imageVector The ImageVector to draw inside the FilledIconButton.
 * @param contentDescription The content description associated with the FilledIconButton.
 * @param modifier Modifier to be applied to the FilledIconButton.
 * @param onClick The callback to be invoked when the FilledIconButton is clicked.
 */
@Composable
fun MMFilledIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FilledIconButton(
        modifier = modifier,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        onClick = onClick
    ) {
        Icon(
            modifier = iconModifier,
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}

@ThemePreviews
@Composable
fun IconButtonPreview() {
    MMTheme {
        MMIconToggleButton(
            checked = true,
            onCheckedChange = { },
            icon = {
                Icon(
                    imageVector = MMIcons.FavoritesOutlined,
                    contentDescription = null,
                )
            },
            checkedIcon = {
                Icon(
                    imageVector = MMIcons.FavoritesFilled,
                    contentDescription = null,
                )
            },
        )
    }
}

@ThemePreviews
@Composable
private fun IconButtonPreviewUnchecked() {
    MMTheme {
        MMIconToggleButton(
            checked = false,
            onCheckedChange = { },
            icon = {
                Icon(
                    imageVector = MMIcons.FavoritesOutlined,
                    contentDescription = null,
                )
            },
            checkedIcon = {
                Icon(
                    imageVector = MMIcons.FavoritesFilled,
                    contentDescription = null,
                )
            },
        )
    }
}
