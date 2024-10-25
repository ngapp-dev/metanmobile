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

package com.ngapp.metanmobile.core.network

import com.ngapp.metanmobile.core.network.model.career.NetworkCareerResource
import com.ngapp.metanmobile.core.network.model.contact.NetworkContactResource
import com.ngapp.metanmobile.core.network.model.faq.NetworkFaqResource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import com.ngapp.metanmobile.core.network.model.price.NetworkPriceResource
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource

/**
 * Interface representing parsing from Metan Mobile backend
 */
interface MetanMobileParserDataSource {
    suspend fun getStations(isRefreshing: Boolean): List<NetworkStationResource>
    suspend fun getStation(stationCode: String, isRefreshing: Boolean): NetworkStationResource?
    suspend fun getFuelPrices(isRefreshing: Boolean): List<NetworkPriceResource>
    suspend fun getFaqList(isRefreshing: Boolean): List<NetworkFaqResource>
    suspend fun getContacts(isRefreshing: Boolean): List<NetworkContactResource>
    suspend fun getNewsList(isRefreshing: Boolean): List<NetworkNewsResource>
    suspend fun getNews(newsId: String, isRefreshing: Boolean): NetworkNewsResource?
    suspend fun getCareerList(isRefreshing: Boolean): List<NetworkCareerResource>
}
