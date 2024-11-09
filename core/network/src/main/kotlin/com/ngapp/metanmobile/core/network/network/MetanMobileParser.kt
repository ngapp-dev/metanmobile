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

import com.ngapp.metanmobile.core.network.BuildConfig
import com.ngapp.metanmobile.core.network.MetanMobileParserDataSource
import com.ngapp.metanmobile.core.network.di.NetworkModule
import com.ngapp.metanmobile.core.network.model.career.NetworkCareerResource
import com.ngapp.metanmobile.core.network.model.career.asNetworkCareerResource
import com.ngapp.metanmobile.core.network.model.contact.NetworkContactResource
import com.ngapp.metanmobile.core.network.model.contact.asNetworkContactResource
import com.ngapp.metanmobile.core.network.model.faq.NetworkFaqResource
import com.ngapp.metanmobile.core.network.model.faq.asNetworkFaqResource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import com.ngapp.metanmobile.core.network.model.news.asNetworkNewsResource
import com.ngapp.metanmobile.core.network.model.price.NetworkPriceResource
import com.ngapp.metanmobile.core.network.model.price.asNetworkPriceResource
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource
import com.ngapp.metanmobile.core.network.model.station.asNetworkStationResource
import com.prof18.rssparser.RssParser
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

private const val METAN_MOBILE_BASE_URL = BuildConfig.METAN_MOBILE_BASE_URL

/**
 * [Retrofit] backed [MetanMobileParserDataSource]
 */
@Singleton
class MetanMobileParser @Inject constructor(
    private val parser: RssParser,
) : MetanMobileParserDataSource {

    private val urlCareer: String = "${METAN_MOBILE_BASE_URL}career/rss/"
    private val urlContacts: String = "${METAN_MOBILE_BASE_URL}contacts/rss/"
    private val urlFaq: String = "${METAN_MOBILE_BASE_URL}faq/rss/"
    private val urlNews = "${METAN_MOBILE_BASE_URL}news/rss/"
    private val urlPrices: String = "${METAN_MOBILE_BASE_URL}calculations/rss/"
    private val urlStations: String = "${METAN_MOBILE_BASE_URL}ecogas-map/rss/"

    override suspend fun getStations(): List<NetworkStationResource> {
        val channel = parser.getRssChannel(urlStations)
        val response = channel.items
        return response.map { it.asNetworkStationResource() }
    }

    override suspend fun getStation(stationCode: String): NetworkStationResource? {
        val channel = parser.getRssChannel(urlStations)
        val response = channel.items
        return response.map { it.asNetworkStationResource() }.find { it.code == stationCode }
    }

    override suspend fun getFuelPrices(): List<NetworkPriceResource> {
        val channel = parser.getRssChannel(urlPrices)
        val response = channel.items
        return response.map { it.asNetworkPriceResource() }
    }

    override suspend fun getFaqList(): List<NetworkFaqResource> {
        val channel = parser.getRssChannel(urlFaq)
        val response = channel.items
        return response.map { it.asNetworkFaqResource() }
    }

    override suspend fun getContacts(): List<NetworkContactResource> {
        val channel = parser.getRssChannel(urlContacts)
        val response = channel.items
        return response.map { it.asNetworkContactResource() }
    }

    override suspend fun getNewsList(): List<NetworkNewsResource> {
        val channel = parser.getRssChannel(urlNews)
        val response = channel.items
        return response.map { it.asNetworkNewsResource() }
    }

    override suspend fun getNews(newsId: String): NetworkNewsResource? {
        val channel = parser.getRssChannel(urlNews)
        val response = channel.items
        return response.map { it.asNetworkNewsResource() }.find { it.id == newsId }
    }

    override suspend fun getCareerList(): List<NetworkCareerResource> {
        val channel = parser.getRssChannel(urlCareer)
        val response = channel.items
        return response.map { it.asNetworkCareerResource() }
    }
}