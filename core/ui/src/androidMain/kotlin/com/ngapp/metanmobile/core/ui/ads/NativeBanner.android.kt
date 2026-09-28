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

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd as GoogleNativeAd
import com.google.android.gms.ads.nativead.MediaView as GoogleMediaView
import com.google.android.gms.ads.nativead.NativeAdView as GoogleNativeAdView
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.designsystem.theme.textColor
import com.yandex.mobile.ads.common.AdRequest as YandexAdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.nativeads.MediaView as YandexMediaView
import com.yandex.mobile.ads.nativeads.NativeAd as YandexNativeAd
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdView as YandexNativeAdView
import com.yandex.mobile.ads.nativeads.NativeAdViewBinder
import dev.icerock.moko.resources.compose.stringResource

// NATIVE_BANNER_AD_UNIT_ID / YANDEX_RU_NATIVE_BANNER_AD_UNIT_ID are generated from
// secrets.properties (see core/ui/build.gradle.kts's generateAdsSecrets task).

@Composable
internal actual fun PlatformNativeBanner(slotKey: String, modifier: Modifier) {
    val context = LocalContext.current
    val style = NativeBannerStyle(
        background = MMColors.cardBackgroundColor.toArgb(),
        text = MMColors.textColor.toArgb(),
        secondaryText = Gray400.toArgb(),
        accent = Blue.toArgb(),
        onAccent = White.toArgb(),
        adLabel = stringResource(SharedRes.strings.core_ui_ad_label),
    )
    if (isRussianAudience(context)) {
        YandexNativeBanner(slotKey, style, modifier)
    } else {
        AdMobNativeBanner(slotKey, style, modifier)
    }
}

/**
 * Loaded ads per placement, kept for the process lifetime: a lazy list disposes a slot scrolled
 * off-screen, and reloading on every scroll back would spam ad requests.
 */
private object NativeAdCache {
    val google = mutableMapOf<String, GoogleNativeAd>()
    val yandex = mutableMapOf<String, YandexNativeAd>()
}

@Composable
private fun AdMobNativeBanner(slotKey: String, style: NativeBannerStyle, modifier: Modifier) {
    val context = LocalContext.current
    var nativeAd by remember(slotKey) { mutableStateOf(NativeAdCache.google[slotKey]) }
    LaunchedEffect(slotKey) {
        if (nativeAd != null) return@LaunchedEffect
        AdLoader.Builder(context.applicationContext, NATIVE_BANNER_AD_UNIT_ID)
            .forNativeAd { ad ->
                NativeAdCache.google.put(slotKey, ad)?.destroy()
                nativeAd = ad
            }
            .withAdListener(object : AdListener() {
                // A failed slot simply stays empty - no retry loop.
                override fun onAdFailedToLoad(error: LoadAdError) = Unit
            })
            .build()
            .loadAd(AdRequest.Builder().build())
    }
    val ad = nativeAd ?: return
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { viewContext ->
            val adView = GoogleNativeAdView(viewContext)
            val mediaView = GoogleMediaView(viewContext).apply {
                setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            }
            val views = NativeBannerViews.create(viewContext, style, mediaView)
            adView.addView(views.root)
            adView.headlineView = views.title
            adView.bodyView = views.body
            adView.iconView = views.icon
            adView.callToActionView = views.callToAction
            adView.advertiserView = views.domain
            adView.mediaView = mediaView
            adView.tag = views
            adView
        },
        update = { adView ->
            val views = adView.tag as NativeBannerViews
            views.title.text = ad.headline
            views.body.setTextOrGone(ad.body)
            views.callToAction.setTextOrGone(ad.callToAction)
            views.domain.setTextOrGone(ad.advertiser)
            views.sponsored.text = style.adLabel
            val icon = ad.icon?.drawable
            views.icon.setImageDrawable(icon)
            views.icon.visibility = if (icon != null) View.VISIBLE else View.GONE
            views.age.visibility = View.GONE
            views.warning.visibility = View.GONE
            views.feedback.visibility = View.GONE
            adView.setNativeAd(ad)
        },
    )
}

@Composable
private fun YandexNativeBanner(slotKey: String, style: NativeBannerStyle, modifier: Modifier) {
    val context = LocalContext.current
    val applicationContext = context.applicationContext
    LaunchedEffect(applicationContext) {
        YandexAdsInitialization.initialize(applicationContext)
    }
    // Same rule as the Yandex banner: nothing is requested before SDK initialization completes.
    if (!YandexAdsInitialization.isInitialized) return

    var nativeAd by remember(slotKey) { mutableStateOf(NativeAdCache.yandex[slotKey]) }
    LaunchedEffect(slotKey) {
        if (nativeAd != null) return@LaunchedEffect
        NativeAdLoader(applicationContext).loadAd(
            YandexAdRequest.Builder(YANDEX_RU_NATIVE_BANNER_AD_UNIT_ID).build(),
            object : NativeAdLoadListener {
                override fun onAdLoaded(ad: YandexNativeAd) {
                    NativeAdCache.yandex[slotKey] = ad
                    nativeAd = ad
                }

                // A failed slot simply stays empty - no retry loop.
                override fun onAdFailedToLoad(error: AdRequestError) = Unit
            },
        )
    }
    val ad = nativeAd ?: return
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { viewContext ->
            val adView = YandexNativeAdView(viewContext)
            val views = NativeBannerViews.create(viewContext, style, YandexMediaView(viewContext))
            adView.addView(views.root)
            adView.tag = views
            adView
        },
        update = { adView ->
            val views = adView.tag as NativeBannerViews
            // Yandex fills every bound view itself and requires all of them to be present.
            runCatching {
                ad.bindNativeAd(
                    NativeAdViewBinder.Builder(adView)
                        .setAgeView(views.age)
                        .setBodyView(views.body)
                        .setCallToActionView(views.callToAction)
                        .setDomainView(views.domain)
                        .setFaviconView(views.favicon)
                        .setFeedbackView(views.feedback)
                        .setIconView(views.icon)
                        .setMediaView(views.media as YandexMediaView)
                        .setSponsoredView(views.sponsored)
                        .setTitleView(views.title)
                        .setWarningView(views.warning)
                        .build(),
                )
            }
        },
    )
}

private class NativeBannerStyle(
    val background: Int,
    val text: Int,
    val secondaryText: Int,
    val accent: Int,
    val onAccent: Int,
    val adLabel: String,
)

/**
 * The NativeBanner layout, built in code (neither SDK ships a ready template in the versions we
 * use): header with icon, title, "Ad" label and advertiser; body; media; call-to-action button.
 */
private class NativeBannerViews(
    val root: LinearLayout,
    val icon: ImageView,
    val favicon: ImageView,
    val feedback: ImageView,
    val title: TextView,
    val sponsored: TextView,
    val domain: TextView,
    val age: TextView,
    val body: TextView,
    val media: View,
    val callToAction: TextView,
    val warning: TextView,
) {
    companion object {
        fun create(context: Context, style: NativeBannerStyle, media: View): NativeBannerViews {
            fun dp(value: Int) = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value.toFloat(),
                context.resources.displayMetrics,
            ).toInt()

            fun text(sizeSp: Float, color: Int, bold: Boolean = false, lines: Int = 1) =
                TextView(context).apply {
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
                    setTextColor(color)
                    if (bold) setTypeface(typeface, Typeface.BOLD)
                    maxLines = lines
                    ellipsize = TextUtils.TruncateAt.END
                }

            val icon = ImageView(context).apply { scaleType = ImageView.ScaleType.CENTER_CROP }
            val favicon = ImageView(context)
            val feedback = ImageView(context)
            val title = text(15f, style.text, bold = true)
            val sponsored = text(11f, style.onAccent).apply {
                background = GradientDrawable().apply {
                    cornerRadius = dp(4).toFloat()
                    setColor(style.accent)
                }
                setPadding(dp(4), 0, dp(4), 0)
            }
            val domain = text(12f, style.secondaryText)
            val age = text(12f, style.secondaryText)
            val body = text(14f, style.text, lines = 2)
            val callToAction = text(14f, style.onAccent, bold = true).apply {
                gravity = Gravity.CENTER
                background = GradientDrawable().apply {
                    cornerRadius = dp(20).toFloat()
                    setColor(style.accent)
                }
            }
            val warning = text(11f, style.secondaryText, lines = 2)

            val meta = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                addView(sponsored)
                addView(favicon, LinearLayout.LayoutParams(dp(12), dp(12)).apply { marginStart = dp(6) })
                addView(domain, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                    marginStart = dp(4)
                })
            }
            val titleColumn = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                addView(title)
                addView(meta)
            }
            val header = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                addView(icon, LinearLayout.LayoutParams(dp(40), dp(40)).apply { marginEnd = dp(12) })
                addView(titleColumn, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(age, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    marginStart = dp(8)
                })
                addView(feedback, LinearLayout.LayoutParams(dp(20), dp(20)).apply { marginStart = dp(8) })
            }
            val mediaContainer = FrameLayout(context).apply {
                addView(media, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(180)))
            }
            val root = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(style.background)
                setPadding(dp(16), dp(12), dp(16), dp(12))
                addView(header)
                addView(body, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = dp(8)
                })
                addView(mediaContainer, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = dp(8)
                })
                addView(callToAction, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(40)).apply {
                    topMargin = dp(10)
                })
                addView(warning, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = dp(4)
                })
            }
            return NativeBannerViews(
                root, icon, favicon, feedback, title, sponsored, domain, age, body, media,
                callToAction, warning,
            )
        }
    }
}

private fun TextView.setTextOrGone(value: String?) {
    text = value
    visibility = if (value.isNullOrBlank()) View.GONE else View.VISIBLE
}
