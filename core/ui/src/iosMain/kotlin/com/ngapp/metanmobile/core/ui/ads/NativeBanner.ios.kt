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

package com.ngapp.metanmobile.core.ui.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.cinterop.ExperimentalForeignApi

/** The row itself is built natively by the Swift bridge (see MobileAdsBridge.swift). */
@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun PlatformNativeBanner(
    slotKey: String,
    layout: NativeBannerLayout,
    modifier: Modifier,
) {
    val bridge = NativeAdsRegistry.bridge ?: return
    val style = NativeAdViewStyle(
        isStationLayout = layout == NativeBannerLayout.Station,
        background = MMColors.cardBackgroundColor.toArgb().toLong(),
        titleColor = MMTypography.titleLarge.color.toArgb().toLong(),
        titleSize = MMTypography.titleLarge.fontSize.value.toDouble(),
        descriptionColor = MMTypography.titleMedium.color.toArgb().toLong(),
        descriptionSize = MMTypography.titleMedium.fontSize.value.toDouble(),
        metaColor = Gray400.toArgb().toLong(),
        metaSize = MMTypography.bodySmall.fontSize.value.toDouble(),
        accent = Blue.toArgb().toLong(),
        onAccent = White.toArgb().toLong(),
        adLabel = stringResource(SharedRes.strings.core_ui_ad_label),
    )
    // Like MainBannerAd: nothing is composed (or takes space) until an ad is actually bound.
    var heightPoints by remember(slotKey, layout) { mutableStateOf(0.0) }
    val adView = remember(slotKey, layout, style.background) {
        bridge.makeNativeAdView(slotKey, style) { height -> heightPoints = height }
    }
    if (heightPoints > 0.0) {
        UIKitView(
            factory = { adView },
            modifier = modifier
                .fillMaxWidth()
                .height(heightPoints.dp),
        )
    }
}
