package com.ngapp.metanmobile.core.network.di

import com.ngapp.metanmobile.core.network.GithubNetworkDataSource
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.network.KtorGithubNetwork
import com.ngapp.metanmobile.core.network.network.KtorMetanEcogasNetwork
import org.koin.core.module.Module
import org.koin.dsl.module

/** Platform-neutral data-source bindings used by Compose on Android and iOS. */
fun sharedNetworkModule(): Module = module {
    single<MetanEcogasNetworkDataSource> { KtorMetanEcogasNetwork(get()) }
    single<GithubNetworkDataSource> { KtorGithubNetwork(get()) }
}
