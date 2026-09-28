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
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView as GoogleNativeAdView
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
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
internal actual fun PlatformNativeBanner(
    slotKey: String,
    layout: NativeBannerLayout,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val style = NativeBannerStyle(
        layout = layout,
        background = MMColors.cardBackgroundColor.toArgb(),
        titleColor = MMTypography.titleLarge.color.toArgb(),
        titleSizeSp = MMTypography.titleLarge.fontSize.value,
        descriptionColor = MMTypography.titleMedium.color.toArgb(),
        descriptionSizeSp = MMTypography.titleMedium.fontSize.value,
        metaColor = Gray400.toArgb(),
        metaSizeSp = MMTypography.bodySmall.fontSize.value,
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
            // The row thumbnail is square; ask for media that fits it.
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_SQUARE)
                    .build(),
            )
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
            adView.bodyView = views.description
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
            views.description.setTextOrGone(ad.body)
            views.callToAction.setTextOrGone(ad.callToAction)
            views.domain.setTextOrGone(ad.advertiser)
            views.sponsored.text = style.adLabel
            // The thumbnail shows the ad's icon at the list rows' own size; ads without an icon
            // show their media there instead, at the 120dp minimum MediaView size.
            val icon = ad.icon?.drawable
            views.icon.setImageDrawable(icon)
            views.icon.visibility = if (icon != null) View.VISIBLE else View.GONE
            views.media.visibility = if (icon != null) View.GONE else View.VISIBLE
            views.setMediaSized(icon == null)
            views.age.visibility = View.GONE
            views.warning.visibility = View.GONE
            views.feedback.visibility = View.GONE
            views.favicon.visibility = View.GONE
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
            // Any Yandex ad may carry media, so the thumbnail always has media size.
            views.setMediaSized(true)
            adView.addView(views.root)
            adView.tag = views
            adView
        },
        update = { adView ->
            val views = adView.tag as NativeBannerViews
            // Yandex fills every bound view itself (hiding the ones the ad has no asset for) and
            // requires all of them to be bound.
            runCatching {
                ad.bindNativeAd(
                    NativeAdViewBinder.Builder(adView)
                        .setAgeView(views.age)
                        .setBodyView(views.description)
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
    val layout: NativeBannerLayout,
    val background: Int,
    val titleColor: Int,
    val titleSizeSp: Float,
    val descriptionColor: Int,
    val descriptionSizeSp: Float,
    val metaColor: Int,
    val metaSizeSp: Float,
    val accent: Int,
    val onAccent: Int,
    val adLabel: String,
)

/**
 * The NativeBanner row, built in code (neither SDK ships a ready template in the versions we use)
 * to match NewsRow / StationRow: the same asymmetric-rounded thumbnail on the left, title
 * (+ description) and a meta line with the "Ad" label, advertiser and the call-to-action. With an
 * icon it's exactly a list row's size; showing media it grows to fit a 120x120dp thumbnail - the
 * minimum media size the ad networks allow - and uses the extra height for the ad's text.
 */
private class NativeBannerViews(
    val root: LinearLayout,
    val thumbnail: FrameLayout,
    private val isStation: Boolean,
    private val density: Float,
    val icon: ImageView,
    val media: View,
    val favicon: ImageView,
    val feedback: ImageView,
    val title: TextView,
    val description: TextView,
    val sponsored: TextView,
    val domain: TextView,
    val age: TextView,
    val callToAction: TextView,
    val warning: TextView,
) {
    fun setMediaSized(mediaSized: Boolean) {
        fun dp(value: Int) = (value * density).toInt()
        thumbnail.layoutParams = (thumbnail.layoutParams as LinearLayout.LayoutParams).apply {
            width = if (mediaSized) dp(120) else dp(74)
            height = if (mediaSized) dp(120) else dp(66)
            marginEnd = if (mediaSized) dp(12) else dp(8)
        }
        description.maxLines = if (mediaSized) 2 else 1
        // News rows only have room for the ad text once the thumbnail makes the row taller.
        if (!isStation && !mediaSized) description.visibility = View.GONE
    }

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

            val isStation = style.layout == NativeBannerLayout.Station

            // Only the icon gets the rows' image shape (RoundedCornerShape(20.dp, 0.dp, 20.dp,
            // 0.dp)); media stays square-cornered so the creative and its video controls are
            // never clipped.
            val corner = dp(20).toFloat()
            val thumbnail = FrameLayout(context)
            val icon = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                background = GradientDrawable().apply {
                    cornerRadii = floatArrayOf(corner, corner, 0f, 0f, corner, corner, 0f, 0f)
                    setColor(style.metaColor and 0x33FFFFFF)
                }
                clipToOutline = true
            }
            thumbnail.addView(media, FrameLayout.LayoutParams(MATCH, MATCH))
            thumbnail.addView(icon, FrameLayout.LayoutParams(MATCH, MATCH))

            val title = text(style.titleSizeSp, style.titleColor, bold = true, lines = if (isStation) 1 else 2)
            val description = text(style.descriptionSizeSp, style.descriptionColor)
            val sponsored = text(style.metaSizeSp, style.onAccent).apply {
                background = GradientDrawable().apply {
                    cornerRadius = dp(4).toFloat()
                    setColor(style.accent)
                }
                setPadding(dp(4), 0, dp(4), 0)
            }
            val favicon = ImageView(context)
            val domain = text(style.metaSizeSp, style.metaColor)
            val age = text(style.metaSizeSp, style.metaColor)
            val callToAction = text(style.metaSizeSp + 1f, style.accent, bold = true)
            val warning = text(style.metaSizeSp - 1f, style.metaColor)
            val feedback = ImageView(context)

            val meta = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                addView(sponsored)
                addView(favicon, LinearLayout.LayoutParams(dp(12), dp(12)).apply { marginStart = dp(6) })
                addView(domain, LinearLayout.LayoutParams(0, WRAP, 1f).apply { marginStart = dp(4) })
                addView(age, LinearLayout.LayoutParams(WRAP, WRAP).apply { marginStart = dp(6) })
                addView(callToAction, LinearLayout.LayoutParams(WRAP, WRAP).apply { marginStart = dp(8) })
            }
            val texts = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                addView(title)
                addView(description, LinearLayout.LayoutParams(MATCH, WRAP).apply { topMargin = dp(2) })
                addView(meta, LinearLayout.LayoutParams(MATCH, WRAP).apply { topMargin = dp(6) })
                addView(warning)
            }

            val root = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setBackgroundColor(style.background)
                setPadding(dp(16), 0, dp(12), 0)
                minimumHeight = dp(82)
                addView(thumbnail, LinearLayout.LayoutParams(dp(74), dp(66)).apply {
                    topMargin = dp(8)
                    bottomMargin = dp(8)
                    marginEnd = dp(8)
                })
                addView(texts, LinearLayout.LayoutParams(0, WRAP, 1f))
                // Yandex requires every icon (the feedback/close control included) to be at least
                // 32x32dp; with its 16dp padding the touch area reaches their 64x64dp minimum.
                addView(feedback, LinearLayout.LayoutParams(dp(32), dp(32)).apply {
                    marginStart = dp(4)
                    gravity = Gravity.TOP
                    topMargin = dp(4)
                })
            }
            return NativeBannerViews(
                root, thumbnail, isStation, context.resources.displayMetrics.density, icon, media, favicon, feedback, title, description, sponsored, domain, age,
                callToAction, warning,
            )
        }

        private const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
        private const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT
    }
}

private fun TextView.setTextOrGone(value: String?) {
    text = value
    visibility = if (value.isNullOrBlank()) View.GONE else View.VISIBLE
}
