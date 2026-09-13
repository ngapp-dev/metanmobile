package com.ngapp.metanmobile.composeapp

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.svg.SvgDecoder

/**
 * Coil3 modularized network support out of `coil-core` — unlike master's Coil 2.x (whose plain
 * `io.coil-kt:coil` artifact bundles an OkHttp fetcher automatically), Coil3 fetches nothing over
 * the network until a `NetworkFetcher` is registered explicitly. That registration was never done
 * during the KMP migration, so every `https://...` image (news/station thumbnails, the GitHub
 * avatar on About, …) silently failed to fetch and fell through to the `error`/`placeholder`
 * painter — this is what wires it up, mirroring master's custom `ImageLoader` (SVG support +
 * `respectCacheHeaders(false)`, since most content images are versioned URLs).
 *
 * Must run before any Coil API is touched (`AsyncImage`, `rememberAsyncImagePainter`, …) —
 * called from both platform entry points ([com.ngapp.metanmobile.MetanMobileApplication] and iOS's
 * `SharedKoin.initSharedKoin`) right alongside Koin setup.
 */
fun configureImageLoader() {
    SingletonImageLoader.setSafe { context: PlatformContext ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
                add(SvgDecoder.Factory())
            }
            // Master also sets respectCacheHeaders(false) (treat content images as versioned
            // URLs rather than trusting server cache headers) — Coil3 dropped that flag with no
            // direct equivalent (cache behavior is now expressed via memory/disk/network
            // CachePolicy instead), so this is deliberately not ported; the primary bug here was
            // simply "no network fetcher at all", which the components above fix.
            .build()
    }
}
