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

package com.ngapp.metanmobile.core.network.model.sync

import com.ngapp.metanmobile.core.network.model.career.NetworkCareerResource
import com.ngapp.metanmobile.core.network.model.contact.NetworkContactResource
import com.ngapp.metanmobile.core.network.model.faq.NetworkFaqResource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import com.ngapp.metanmobile.core.network.model.price.NetworkPriceResource
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource
import kotlinx.serialization.Serializable

/**
 * Response of `GET /api/sync?since=<version>`. `deleted` is currently always empty — the worker
 * doesn't have tombstones yet (needs a full site-listing crawl, not just the truncated RSS window
 * — see the sync spec's Этап 3), so nothing is ever removed via this response today.
 */
@Serializable
data class NetworkFeedDelta<T>(
    val upserted: List<T> = emptyList(),
    val deleted: List<String> = emptyList(),
    val error: String? = null,
)

@Serializable
data class NetworkSyncFeeds(
    val news: NetworkFeedDelta<NetworkNewsResource> = NetworkFeedDelta(),
    val stations: NetworkFeedDelta<NetworkStationResource> = NetworkFeedDelta(),
    val faq: NetworkFeedDelta<NetworkFaqResource> = NetworkFeedDelta(),
    val contacts: NetworkFeedDelta<NetworkContactResource> = NetworkFeedDelta(),
    val career: NetworkFeedDelta<NetworkCareerResource> = NetworkFeedDelta(),
    val prices: NetworkFeedDelta<NetworkPriceResource> = NetworkFeedDelta(),
)

@Serializable
data class NetworkSyncResponse(
    val version: Long,
    val feeds: NetworkSyncFeeds,
)
