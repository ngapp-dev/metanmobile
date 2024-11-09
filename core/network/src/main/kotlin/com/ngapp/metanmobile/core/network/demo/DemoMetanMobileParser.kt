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

package com.ngapp.metanmobile.core.network.demo

import JvmUnitTestDemoAssetManager
import com.ngapp.metanmobile.core.common.network.Dispatcher
import com.ngapp.metanmobile.core.common.network.MMDispatchers.IO
import com.ngapp.metanmobile.core.network.MetanMobileParserDataSource
import com.ngapp.metanmobile.core.network.model.career.NetworkCareerResource
import com.ngapp.metanmobile.core.network.model.contact.NetworkContactResource
import com.ngapp.metanmobile.core.network.model.faq.NetworkFaqResource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import com.ngapp.metanmobile.core.network.model.price.NetworkPriceResource
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [MetanMobileParserDataSource] implementation that provides static resources to aid development
 */
@OptIn(ExperimentalSerializationApi::class)
@Singleton
class DemoMetanMobileParser @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkJson: Json,
    private val assets: DemoAssetManager = JvmUnitTestDemoAssetManager,
) : MetanMobileParserDataSource {

    override suspend fun getStations(): List<NetworkStationResource> =
        withContext(ioDispatcher) {
            assets.open(STATIONS_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getStation(stationCode: String): NetworkStationResource? =
        withContext(ioDispatcher) {
            assets.open(STATIONS_ASSET).use { stream ->
                val stations = networkJson.decodeFromStream<List<NetworkStationResource>>(stream)
                stations.find { it.code == stationCode }
            }
        }

    override suspend fun getFuelPrices(): List<NetworkPriceResource> =
        withContext(ioDispatcher) {
            assets.open(PRICES_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getFaqList(): List<NetworkFaqResource> =
        withContext(ioDispatcher) {
            assets.open(FAQ_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getContacts(): List<NetworkContactResource> =
        withContext(ioDispatcher) {
            assets.open(CONTACTS_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getNewsList(): List<NetworkNewsResource> =
        withContext(ioDispatcher) {
            assets.open(NEWS_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getNews(newsId: String): NetworkNewsResource? =
        withContext(ioDispatcher) {
            assets.open(NEWS_ASSET).use { stream ->
                val stations = networkJson.decodeFromStream<List<NetworkNewsResource>>(stream)
                stations.find { it.id == newsId }
            }
        }

    override suspend fun getCareerList(): List<NetworkCareerResource> =
        withContext(ioDispatcher) {
            assets.open(CAREERS_ASSET).use(networkJson::decodeFromStream)
        }

    companion object {
        private const val STATIONS_ASSET = "stations.json"
        private const val PRICES_ASSET = "prices.json"
        private const val FAQ_ASSET = "faq.json"
        private const val CONTACTS_ASSET = "contacts.json"
        private const val NEWS_ASSET = "news.json"
        private const val CAREERS_ASSET = "careers.json"
    }
}