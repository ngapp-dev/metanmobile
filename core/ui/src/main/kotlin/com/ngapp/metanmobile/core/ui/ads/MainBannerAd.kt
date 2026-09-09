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
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.ngapp.metanmobile.core.ui.BuildConfig

private const val TAG = "MainBannerAd"

@SuppressLint("MissingPermission")
@Composable
fun MainBannerAd(
    adUnit: String = BuildConfig.MAIN_BANNER_AD_ID_KEY,
) {
    val context = LocalContext.current
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = adUnit
            // loadAd() used to fail silently - with no listener, a bad ad unit ID, a device that
            // isn't registered as a test device (see ConsentHelper.initializeMobileAdsSdk), or a
            // plain no-fill all looked identical to "everything is fine": consent granted, banner
            // composed, nothing on screen. Logcat with this tag now shows which one it actually is.
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d(TAG, "Ad loaded for unit $adUnitId")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "Ad failed to load for unit $adUnitId: ${error.code} ${error.message}")
                }
            }
            loadAd(AdRequest.Builder().build())
        }
    }
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { adView }
    )
}

/**
 * Shows [bannerAd] only once consent has been resolved and ads are allowed to load - this is the
 * gate between [ConsentHelper] and the actual ad view, kept as its own composable so it can be
 * tested with a fake [bannerAd] instead of loading a real ad.
 *
 * @param canShowAds Mirrors [ConsentHelper.canShowAds].
 * @param bannerAd The ad to show once allowed. Defaults to [MainBannerAd].
 */
@Composable
fun ConsentGatedBannerAd(
    canShowAds: Boolean,
    bannerAd: @Composable () -> Unit = { MainBannerAd() },
) {
    if (canShowAds) {
        bannerAd()
    }
}
