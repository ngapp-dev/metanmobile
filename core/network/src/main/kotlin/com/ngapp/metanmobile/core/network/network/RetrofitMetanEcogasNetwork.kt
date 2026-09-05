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

package com.ngapp.metanmobile.core.network.network

import androidx.tracing.trace
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ngapp.metanmobile.core.network.BuildConfig
import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.model.career.NetworkCareerResource
import com.ngapp.metanmobile.core.network.model.contact.NetworkContactResource
import com.ngapp.metanmobile.core.network.model.faq.NetworkFaqResource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import com.ngapp.metanmobile.core.network.model.price.NetworkPriceResource
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API declaration for Metan Ecogas Network API
 */
private interface RetrofitMetanEcogasNetworkApi {
    @GET(value = "api/stations")
    suspend fun getStations(): NetworkResponse<List<NetworkStationResource>>

    @GET(value = "api/stations/{stationCode}")
    suspend fun getStation(stationCode: String): NetworkResponse<NetworkStationResource>

    @GET(value = "api/prices")
    suspend fun getFuelPrices(): NetworkResponse<List<NetworkPriceResource>>

    @GET(value = "api/faq")
    suspend fun getFaqList(): NetworkResponse<List<NetworkFaqResource>>

    @GET(value = "api/contacts")
    suspend fun getContacts(): NetworkResponse<List<NetworkContactResource>>

    @GET(value = "api/news")
    suspend fun getNewsList(): NetworkResponse<List<NetworkNewsResource>>

    @GET(value = "api/news/{newsId}")
    suspend fun getNews(newsId: String): NetworkResponse<NetworkNewsResource>

    @GET(value = "api/career")
    suspend fun getCareerList(): NetworkResponse<List<NetworkCareerResource>>
}

private const val BASE_URL = BuildConfig.METAN_ECOGAS_API

/**
 * Wrapper for data provided from the [METAN_ECOGAS_API]
 */
@Serializable
private data class NetworkResponse<T>(
    val data: T,
    val error: String? = null,
)

/**
 * Returns [NetworkResponse.data] unless the server reports the feed as failed
 * ([NetworkResponse.error] non-null) AND has no cached items to fall back on. In that case there
 * is genuinely nothing to show, so it is surfaced as a failure rather than silently looking like
 * "there is no content".
 */
private fun <T> NetworkResponse<List<T>>.dataOrThrow(): List<T> =
    data.ifEmpty { if (error != null) throw ApiFeedException(error) else data }

/**
 * [Retrofit] backed [MetanEcogasNetworkDataSource]
 */
@Singleton
internal class RetrofitMetanEcogasNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : MetanEcogasNetworkDataSource {

    private val networkApi = trace("RetrofitMetanEcogasNetwork") {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitMetanEcogasNetworkApi::class.java)
    }

    override suspend fun getStations(): List<NetworkStationResource> = networkApi.getStations().dataOrThrow()
    override suspend fun getStation(stationCode: String): NetworkStationResource =
        networkApi.getStation(stationCode).data

    override suspend fun getFuelPrices(): List<NetworkPriceResource> =
        networkApi.getFuelPrices().dataOrThrow()

    override suspend fun getFaqList(): List<NetworkFaqResource> = networkApi.getFaqList().dataOrThrow()
    override suspend fun getContacts(): List<NetworkContactResource> = networkApi.getContacts().dataOrThrow()
    override suspend fun getNewsList(): List<NetworkNewsResource> = networkApi.getNewsList().dataOrThrow()
    override suspend fun getNews(newsId: String): NetworkNewsResource =
        networkApi.getNews(newsId).data

    override suspend fun getCareerList(): List<NetworkCareerResource> =
        networkApi.getCareerList().dataOrThrow()

}