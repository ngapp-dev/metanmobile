package com.ngapp.metanmobile.core.network.di

import coil3.ImageLoader
import coil3.svg.SvgDecoder
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.GithubNetworkDataSource
import com.ngapp.metanmobile.core.network.client.MetanEcogasHttpClient
import com.ngapp.metanmobile.core.network.network.KtorMetanEcogasNetwork
import com.ngapp.metanmobile.core.network.network.KtorGithubNetwork
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/** Koin bindings for the Cloudflare-backed network API. */
fun metanEcogasNetworkModule(): Module = module {
    single<MetanEcogasNetworkDataSource> {
        KtorMetanEcogasNetwork(client = get<MetanEcogasHttpClient>())
    }
    single<GithubNetworkDataSource> { KtorGithubNetwork(get()) }
    single<ImageLoader> {
        ImageLoader.Builder(androidContext())
            .components { add(SvgDecoder.Factory()) }
            .build()
    }
}
