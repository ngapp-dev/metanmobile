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

package com.ngapp.metanmobile.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray250
import com.ngapp.metanmobile.core.designsystem.theme.Gray500
import com.ngapp.metanmobile.core.designsystem.theme.LightBlue
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.designsystem.R as designsystemR

@Composable
fun CarTypesRadioGroup(
    items: List<Pair<String, Int>>,
    selected: String,
    setSelected: (selected: Pair<String, Int>) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top,
    ) {
        items.forEach { item ->
            Row(
                modifier = Modifier.selectable(
                    selected = (selected == item.first),
                    onClick = { setSelected(item) },
                    role = Role.RadioButton,
                )
            ) {
                RadioButtonStyle(selectedItem = selected, item = item)
            }
        }
    }
}


@Composable
private fun RadioButtonStyle(selectedItem: String, item: Pair<String, Int>) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (selectedItem == item.first) Blue else Gray250,
                shape = RoundedCornerShape(100),
            )
            .clip(RoundedCornerShape(100))
            .background(if (selectedItem == item.first) LightBlue.copy(alpha = 0.5f) else White),
    ) {
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(item.second)
                .size(74)
                .build()
        )
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(
                R.string.core_ui_description_radio_button_img,
                item.first,
            ),
            modifier = Modifier
                .clip(CircleShape)
                .size(74.dp)
        )
    }
}