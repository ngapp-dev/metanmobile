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

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest as YandexAdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.YandexAds
import java.util.Locale

private const val RUSSIAN_COUNTRY_CODE = "RU"

// MAIN_BANNER_AD_UNIT_ID is generated at build time from secrets.properties' MAIN_BANNER_AD_ID_KEY
// (see core/ui/build.gradle.kts's generateAdsSecrets task) - falls back to Google's own public
// test unit id if secrets.properties is missing. YANDEX_RU_BANNER_AD_UNIT_ID is generated from
// the same file's YANDEX_RU_BANNER_AD_ID_KEY and falls back to Yandex's demo id.

/** Renders exactly one banner network: Yandex in Russia, otherwise the existing AdMob banner. */
@Composable
actual fun MainBannerAd() {
    val context = LocalContext.current
    if (isRussianAudience(context)) {
        YandexRuBannerAd(context)
    } else {
        AdMobBannerAd(context)
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun AdMobBannerAd(context: Context) {
    // The AndroidView below is only composed once an ad has actually loaded, so an empty/failed
    // slot reserves no space (matches how this looked before AdListener existed at all - now it's
    // deliberate instead of incidental, and symmetric with iOS's MainBannerAd.ios.kt). The AdView
    // itself, and its loadAd() call, still happen unconditionally via remember{} below regardless
    // of isAdLoaded's value, so the request fires immediately rather than only once composed.
    var isAdLoaded by remember { mutableStateOf(false) }
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = MAIN_BANNER_AD_UNIT_ID
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    isAdLoaded = true
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isAdLoaded = false
                }
            }
            loadAd(AdRequest.Builder().build())
        }
    }
    DisposableEffect(adView) {
        onDispose(adView::destroy)
    }
    if (isAdLoaded) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { adView }
        )
    }
}

@Composable
private fun YandexRuBannerAd(context: Context) {
    val applicationContext = context.applicationContext
    LaunchedEffect(applicationContext) {
        YandexAdsInitialization.initialize(applicationContext)
    }

    // Do not construct or request the Yandex banner until the SDK initialization callback.
    if (!YandexAdsInitialization.isInitialized) return

    var isAdLoaded by remember(YANDEX_RU_BANNER_AD_UNIT_ID) { mutableStateOf(false) }
    val adView = remember(context) {
        BannerAdView(context).apply {
            // Preserve the existing fixed AdMob BANNER footprint: 320 x 50 dp.
            setAdSize(BannerAdSize.fixed(context, 320, 50))
            setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() {
                    isAdLoaded = true
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    // No retry here: a failed banner must not reserve space or create a request loop.
                    isAdLoaded = false
                }

                override fun onAdClicked() = Unit

                override fun onImpression(impressionData: ImpressionData?) = Unit
            })
            loadAd(YandexAdRequest.Builder(YANDEX_RU_BANNER_AD_UNIT_ID).build())
        }
    }
    DisposableEffect(adView) {
        onDispose(adView::destroy)
    }
    if (isAdLoaded) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { adView }
        )
    }
}

internal object YandexAdsInitialization {
    var isInitialized by mutableStateOf(false)
        private set
    private var isInitializationStarted = false

    fun initialize(context: Context) {
        if (isInitializationStarted) return
        isInitializationStarted = true
        YandexAds.initialize(context) {
            isInitialized = true
        }
    }
}

internal fun isRussianAudience(context: Context): Boolean = countryCode(context) == RUSSIAN_COUNTRY_CODE

private fun countryCode(context: Context): String {
    val networkCountry = runCatching {
        context.getSystemService(TelephonyManager::class.java).networkCountryIso
    }.getOrNull().orEmpty()
    return (networkCountry.ifBlank { Locale.getDefault().country }).uppercase(Locale.ROOT)
}
