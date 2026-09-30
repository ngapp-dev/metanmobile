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
 * MetanMobileApp.swift right alongside initSharedKoin(). [ConsentHelper], [MainBannerAd] and
 * [NativeBanner] on iOS all just forward to whatever is registered here.
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

    /**
     * A NativeBanner row for [NativeBanner] (AdMob, or Yandex for Russian users), laid out like
     * the surrounding list's rows per [style]. The loaded ad is cached per [slotKey] for the app's
     * lifetime, so a lazy-list slot recreated on scroll doesn't request a new ad.
     * [onAdLoadResult] reports the row's height in points once an ad is bound to the returned
     * view, and 0 on a failed/no-fill load - the view is only shown after a positive height.
     */
    fun makeNativeAdView(
        slotKey: String,
        style: NativeAdViewStyle,
        onAdLoadResult: (heightPoints: Double) -> Unit,
    ): UIView
}

/**
 * Look of a native ad row, resolved from the Compose theme on the Kotlin side. Colors are ARGB.
 *
 * @property isStationLayout StationRow-like (one-line title + description) instead of NewsRow-like.
 */
class NativeAdViewStyle(
    val isStationLayout: Boolean,
    val background: Long,
    val titleColor: Long,
    val titleSize: Double,
    val descriptionColor: Long,
    val descriptionSize: Double,
    val metaColor: Long,
    val metaSize: Double,
    val accent: Long,
    val onAccent: Long,
    val adLabel: String,
)

/** Set exactly once, from Swift, before any of [ConsentHelper]'s methods are used for real. */
object NativeAdsRegistry {
    var bridge: NativeAdsBridge? = null
}

/** Called from Swift (MetanMobileApp.swift) to install the real ads implementation. */
fun registerNativeAdsBridge(bridge: NativeAdsBridge) {
    NativeAdsRegistry.bridge = bridge
}
