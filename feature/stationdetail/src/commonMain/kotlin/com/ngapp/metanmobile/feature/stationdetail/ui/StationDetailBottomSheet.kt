@file:OptIn(ExperimentalMaterial3Api::class)

package com.ngapp.metanmobile.feature.stationdetail.ui

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.theme.Gray500
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.ui.util.BackHandler
import com.ngapp.metanmobile.feature.stationdetail.StationDetailRoute
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailBottomSheet(
    stationCode: String?,
    bottomSheetState: BottomSheetScaffoldState,
    onShowTopAppBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    onNewsDetailClick: (String) -> Unit,
    content: @Composable (Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetValue = bottomSheetState.bottomSheetState.currentValue
    // LocalConfiguration is Android-only — LocalWindowInfo (Compose UI core, multiplatform) +
    // density gives the same screen-height-in-dp cross-platform.
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val screenHeightDp = with(density) { windowInfo.containerSize.height.toDp() }

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
        sheetPeekHeight = screenHeightDp * 0.4f,
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
                    StationDetailRoute(
                        stationCode = stationCode,
                        onNewsDetailClick = onNewsDetailClick,
                        onBackClick = {
                            coroutineScope.launch {
                                bottomSheetState.bottomSheetState.hide()
                                onShowTopAppBar(true)
                                onShowBottomBar(true)
                            }
                        },
                    )
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
