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
    // GithubHttpDataSource (from networkClientModule()) is registered unqualified — only its own
    // MetanEcogasHttpClient dependency is qualified "github", to pick the right base URL among
    // several registered HTTP clients. get(named("github")) here was resolving the wrong type
    // against a qualifier nothing was ever registered under, throwing NoDefinitionFoundException
    // the moment anything actually tried to sync GithubUserRepository — invisible on Android
    // (metanEcogasNetworkModule() already does this correctly with a plain get()) and, until
    // sync actually started running on iOS, invisible there too.
    single<GithubNetworkDataSource> { KtorGithubNetwork(get()) }
}
