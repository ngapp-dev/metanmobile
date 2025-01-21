/*
 * Copyright 2025 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.core.ui.stations.stationDetail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.theme.Gray500
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailBottomSheet(
    stationCode: String?,
    bottomSheetState: BottomSheetScaffoldState,
    openFullScreen: Boolean = false,
    onShowTopAppBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    sheetContent: @Composable () -> Unit,
    content: @Composable (Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetValue = bottomSheetState.bottomSheetState.currentValue
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val roundedCornerShape by animateDpAsState(
        targetValue = if (bottomSheetValue == SheetValue.Expanded) 0.dp else 16.dp,
        animationSpec = tween(durationMillis = 150)
    )

    val updateUI: () -> Unit = {
        when (bottomSheetValue) {
            SheetValue.Expanded -> {
                onShowTopAppBar(false)
                onShowBottomBar(false)
            }

            SheetValue.PartiallyExpanded -> {
                onShowTopAppBar(true)
                onShowBottomBar(false)
            }

            else -> {
                onShowTopAppBar(true)
                onShowBottomBar(true)
            }
        }
    }

    LaunchedEffect(bottomSheetValue) {
        updateUI()
    }

    BackHandler(enabled = bottomSheetValue != SheetValue.Hidden) {
        coroutineScope.launch {
            bottomSheetState.bottomSheetState.hide()
            onShowTopAppBar(true)
            onShowBottomBar(true)
        }
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetPeekHeight = if (openFullScreen) screenHeight.dp else (screenHeight * 0.4).dp,
        sheetShape = RoundedCornerShape(
            topStart = roundedCornerShape,
            topEnd = roundedCornerShape,
        ),
        sheetDragHandle = null,
        sheetContainerColor = MMColors.cardBackgroundColor,
        sheetTonalElevation = 10.dp,
        sheetContent = {
            Column {
                BottomSheetDragHandle()
                if (!stationCode.isNullOrEmpty()) {
                    sheetContent()
                }
            }
        },
    ) {
        content(bottomSheetState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded)
    }
}

@Composable
private fun BottomSheetDragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp, 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Gray500)
                .align(Alignment.Center)
        ) {}
    }
}