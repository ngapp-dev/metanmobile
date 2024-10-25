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

package com.ngapp.metanmobile.core.network.network

import androidx.tracing.trace
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ngapp.metanmobile.core.network.BuildConfig
import com.ngapp.metanmobile.core.network.GithubNetworkDataSource
import com.ngapp.metanmobile.core.network.model.githubuser.NetworkGithubUserResource
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API declaration for Github Network API
 */
private interface RetrofitGithubNetworkApi {
    @GET(value = "users/ngapp-dev")
    suspend fun getGithubUser(): NetworkGithubUserResource
}

private const val GITHUB_BASE_URL = BuildConfig.GITHUB_BASE_URL

/**
 * [Retrofit] backed [GithubNetworkDataSource]
 */
@Singleton
internal class RetrofitGithubNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : GithubNetworkDataSource {

    private val networkApi = trace("RetrofitGithubNetwork") {
        Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitGithubNetworkApi::class.java)
    }

    override suspend fun getGithubUser(): NetworkGithubUserResource = networkApi.getGithubUser()
}