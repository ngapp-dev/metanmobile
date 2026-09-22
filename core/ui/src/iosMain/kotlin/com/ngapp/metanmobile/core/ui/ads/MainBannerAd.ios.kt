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

package com.ngapp.metanmobile.core.ui.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle

// Info.plist's MMMainBannerAdUnitID is patched in at Xcode build time from secrets.properties'
// MAIN_BANNER_AD_ID_KEY (see the "Inject Ads Secrets" run script build phase) - falls back to
// Google's own public iOS test banner unit id, which always serves a real (test) ad, if that key
// is missing (e.g. a fresh checkout that hasn't set up secrets.properties yet).
private const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/2934735716"

private val mainBannerAdUnitId: String
    get() = (NSBundle.mainBundle.objectForInfoDictionaryKey("MMMainBannerAdUnitID") as? String)
        ?.takeIf { it.isNotBlank() && !it.startsWith("$") }
        ?: TEST_BANNER_AD_UNIT_ID

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MainBannerAd() {
    val bridge = NativeAdsRegistry.bridge ?: return
    // Only composed once an ad has actually loaded, so an empty/failed slot reserves no layout
    // space (matches Android's AdListener-driven MainBannerAd). makeBannerAdView() still creates
    // the native banner view and kicks off its load unconditionally via remember{} below,
    // regardless of isAdLoaded - the request fires immediately rather than only once shown.
    var isAdLoaded by remember { mutableStateOf(false) }
    val bannerView = remember {
        bridge.makeBannerAdView(mainBannerAdUnitId) { loaded -> isAdLoaded = loaded }
    }
    if (isAdLoaded) {
        UIKitView(
            factory = { bannerView },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
