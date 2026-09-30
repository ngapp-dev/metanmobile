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

package com.ngapp.metanmobile.core.ui.animation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMButton
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White

@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    e: Throwable? = null,
    error: String = "",
    action: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(horizontal = 16.dp)
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PulsingIcon(
                icon = MMIcons.ErrorOutlined,
                contentDescription = error.takeIf { it.isNotEmpty() } ?: e?.message,
                size = 96.dp,
                tint = MaterialTheme.colorScheme.error,
            )
        }
        Spacer(Modifier.height(12.dp))
        e?.message?.let {
            Text(
                text = it,
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                style = MMTypography.headlineMedium
            )
        }
        if (error.isNotEmpty()) {
            Text(
                text = error,
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                style = MMTypography.headlineMedium
            )
        }
        Spacer(Modifier.height(12.dp))
        MMButton(
            buttonText = SharedRes.strings.core_ui_button_retry,
            buttonBackgroundColor = Blue,
            fontColor = White,
            borderStrokeColor = Blue,
            onClick = { action.invoke() },
            modifier = modifier.padding(vertical = 4.dp)
        )
    }
}
