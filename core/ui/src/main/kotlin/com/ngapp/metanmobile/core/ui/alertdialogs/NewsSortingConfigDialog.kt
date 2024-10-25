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

package com.ngapp.metanmobile.core.ui.alertdialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.component.MMTextButton
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.Red700
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingType
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.ui.R

@Composable
fun NewsSortingConfigDialog(
    newsSortingConfig: NewsSortingConfig,
    onConfirmClick: (NewsSortingConfig) -> Unit,
    onShowAlertDialog: (Boolean) -> Unit,
) {
    val configuration = LocalConfiguration.current

    var selectedSortingType by rememberSaveable { mutableStateOf(newsSortingConfig.sortingType) }
    var selectedSortingOrder by rememberSaveable { mutableStateOf(newsSortingConfig.sortingOrder) }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = { onShowAlertDialog(false) },
        title = {
            Text(
                text = stringResource(R.string.core_ui_title_filter_and_sort),
                style = MMTypography.displayMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.core_ui_text_sort_by),
                    style = MMTypography.titleLarge
                )
                SortTypesRadioGroup(
                    items = NewsSortingType.entries,
                    selected = selectedSortingType,
                    setSelected = { selectedSortingType = it }
                )
                MMDivider()
                SortAscDescRadioGroup(
                    items = SortingOrder.entries,
                    selected = selectedSortingOrder,
                    setSelected = { selectedSortingOrder = it }
                )
            }
        },
        confirmButton = {
            MMTextButton(
                onClick = {
                    onConfirmClick(
                        newsSortingConfig.copy(
                            sortingType = selectedSortingType,
                            sortingOrder = selectedSortingOrder,
                        )
                    )
                    onShowAlertDialog(false)
                },
                text = { Text(text = stringResource(R.string.core_ui_button_apply)) }
            )
        },
        dismissButton = {
            MMTextButton(onClick = { onShowAlertDialog(false) },
                text = { Text(text = stringResource(R.string.core_ui_button_cancel)) }
            )
        },
    )
}

@Composable
private fun SortTypesRadioGroup(
    items: List<NewsSortingType>,
    selected: NewsSortingType,
    setSelected: (NewsSortingType) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        items.forEach { item ->
            val sortingTypeText = getSortingTypeText(item)
            Row(
                modifier = Modifier
                    .offset((-12).dp)
                    .selectable(
                        selected = selected == item,
                        role = Role.RadioButton,
                        onClick = { setSelected(item) },
                    )
                    .semantics { contentDescription = sortingTypeText },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == item,
                    onClick = { setSelected(item) },
                    enabled = true,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Red700,
                        unselectedColor = Blue,
                        disabledUnselectedColor = Gray400,
                        disabledSelectedColor = Gray400
                    )
                )
                Text(
                    text = sortingTypeText,
                    style = MMTypography.titleMedium
                )
            }
        }
    }
}

@Composable
fun SortAscDescRadioGroup(
    items: List<SortingOrder>,
    selected: SortingOrder,
    setSelected: (SortingOrder) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        items.forEach { item ->
            val sortingOrderText = getSortingOrderText(item)
            Row(
                modifier = Modifier
                    .offset((-12).dp)
                    .selectable(
                        selected = selected == item,
                        role = Role.RadioButton,
                        onClick = { setSelected(item) },
                    )
                    .semantics { contentDescription = sortingOrderText },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == item,
                    onClick = { setSelected(item) },
                    enabled = true,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Red700,
                        unselectedColor = Blue,
                        disabledUnselectedColor = Gray400,
                        disabledSelectedColor = Gray400,
                    )
                )
                Text(
                    text = sortingOrderText,
                    style = MMTypography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun getSortingTypeText(sortingType: NewsSortingType): String {
    return when (sortingType) {
        NewsSortingType.DATE -> stringResource(R.string.core_ui_button_date)
        NewsSortingType.NAME -> stringResource(R.string.core_ui_button_name)
    }
}

@Composable
fun getSortingOrderText(sortingOrder: SortingOrder): String {
    return when (sortingOrder) {
        SortingOrder.ASC -> stringResource(R.string.core_ui_button_ascending)
        SortingOrder.DESC -> stringResource(R.string.core_ui_button_descending)
    }
}