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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

/**
 * Whether ad consent allows showing ads - provided once at the app root from
 * [ConsentHelper.canShowAds], so in-list [NativeBanner]s don't each need the helper injected.
 */
val LocalCanShowAds = compositionLocalOf { false }

/** Which list row a [NativeBanner] mimics, so the ad sits in the list like one of its items. */
enum class NativeBannerLayout {
    /** Like NewsRow: thumbnail, two-line title, meta line. Also used for FAQ, careers, menu. */
    News,

    /** Like StationRow: thumbnail, one-line title, one-line description, meta line. */
    Station,
}

/**
 * Native ad block placed inside content lists (every N items) and at the end of the menu, drawn
 * as a row of the surrounding list ([layout]). Shows Yandex in Russia and AdMob elsewhere, like
 * [MainBannerAd]; takes no space until an ad has actually loaded, and none without ad consent.
 *
 * @param slotKey stable id of this placement (e.g. "stations-8"). The loaded ad is cached per slot
 * for the process, so scrolling a slot out of and back into a lazy list doesn't request a new ad.
 */
@Composable
fun NativeBanner(
    slotKey: String,
    modifier: Modifier = Modifier,
    layout: NativeBannerLayout = NativeBannerLayout.News,
) {
    if (LocalCanShowAds.current) PlatformNativeBanner(slotKey, layout, modifier)
}

/** Whether a native banner goes after the item at [index] when shown every [interval] items. */
fun isNativeBannerSlot(index: Int, interval: Int, itemCount: Int): Boolean =
    (index + 1) % interval == 0 && index != itemCount - 1

@Composable
internal expect fun PlatformNativeBanner(
    slotKey: String,
    layout: NativeBannerLayout,
    modifier: Modifier,
)
