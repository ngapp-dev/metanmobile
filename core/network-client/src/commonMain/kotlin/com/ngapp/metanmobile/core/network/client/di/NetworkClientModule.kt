package com.ngapp.metanmobile.core.network.client.di

import com.ngapp.metanmobile.core.network.client.GithubHttpDataSource
import com.ngapp.metanmobile.core.network.client.MetanEcogasHttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Platform-neutral Koin module for the Cloudflare API client.
 *
 * The host application supplies the URL, so the same graph is valid for Android and iOS.
 */
fun networkClientModule(baseUrl: String, githubBaseUrl: String): Module = module {
    single { MetanEcogasHttpClient(baseUrl = baseUrl) }
    single(named("github")) { MetanEcogasHttpClient(baseUrl = githubBaseUrl) }
    single { GithubHttpDataSource(get(named("github"))) }
}
