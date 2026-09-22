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

import platform.UIKit.UIView

/**
 * The GoogleMobileAds/UserMessagingPlatform SDKs are only linked into this app via the Xcode
 * project's own Swift Package Manager dependencies (see iosApp/MetanMobile.xcodeproj) - they are
 * invisible to the separate Kotlin/Native compilation of this shared framework, which has no
 * cinterop bindings for them. This interface is the seam: implemented in Swift
 * (MobileAdsBridge.swift) and handed to Kotlin once via [registerNativeAdsBridge], called from
 * MetanMobileApp.swift right alongside initSharedKoin(). [ConsentHelper] and [MainBannerAd] on
 * iOS both just forward to whatever is registered here.
 */
interface NativeAdsBridge {
    fun initializeMobileAdsSdk()
    fun isPrivacyOptionsRequired(): Boolean
    fun updateConsent(onResult: (canShowAds: Boolean) -> Unit)
    fun obtainConsentAndShow(onResult: (canShowAds: Boolean) -> Unit)
    fun revokeConsent()

    /**
     * [onAdLoadResult] fires true once the returned view actually has an ad to show, false on a
     * failed/no-fill load - [MainBannerAd] only composes this view once that first fires true, so
     * an empty/failed banner reserves no layout space (same as Android's AdListener-driven
     * [MainBannerAd]).
     */
    fun makeBannerAdView(adUnitId: String, onAdLoadResult: (loaded: Boolean) -> Unit): UIView
}

/** Set exactly once, from Swift, before any of [ConsentHelper]'s methods are used for real. */
object NativeAdsRegistry {
    var bridge: NativeAdsBridge? = null
}

/** Called from Swift (MetanMobileApp.swift) to install the real ads implementation. */
fun registerNativeAdsBridge(bridge: NativeAdsBridge) {
    NativeAdsRegistry.bridge = bridge
}
