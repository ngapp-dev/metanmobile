package com.ngapp.metanmobile.core.network.network

import com.ngapp.metanmobile.core.network.MetanEcogasNetworkDataSource
import com.ngapp.metanmobile.core.network.client.CloudflareResponse
import com.ngapp.metanmobile.core.network.client.MetanEcogasHttpClient
import com.ngapp.metanmobile.core.network.model.career.NetworkCareerResource
import com.ngapp.metanmobile.core.network.model.contact.NetworkContactResource
import com.ngapp.metanmobile.core.network.model.faq.NetworkFaqResource
import com.ngapp.metanmobile.core.network.model.news.NetworkNewsResource
import com.ngapp.metanmobile.core.network.model.price.NetworkPriceResource
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource
import com.ngapp.metanmobile.core.network.model.sync.NetworkSyncResponse

internal class KtorMetanEcogasNetwork(
    private val client: MetanEcogasHttpClient,
) : MetanEcogasNetworkDataSource {
    override suspend fun getStations() = client.get<CloudflareResponse<List<NetworkStationResource>>>("api/stations").items()
    override suspend fun getFuelPrices() = client.get<CloudflareResponse<List<NetworkPriceResource>>>("api/prices").items()
    override suspend fun getFaqList() = client.get<CloudflareResponse<List<NetworkFaqResource>>>("api/faq").items()
    override suspend fun getContacts() = client.get<CloudflareResponse<List<NetworkContactResource>>>("api/contacts").items()
    override suspend fun getNewsList() = client.get<CloudflareResponse<List<NetworkNewsResource>>>("api/news").items()
    override suspend fun getCareerList() = client.get<CloudflareResponse<List<NetworkCareerResource>>>("api/career").items()
    override suspend fun getSync(since: Long) = client.get<NetworkSyncResponse>("api/sync?since=$since")
}

private fun <T> CloudflareResponse<List<T>>.items(): List<T> {
    val responseError = error
    return data.ifEmpty { if (responseError != null) throw ApiFeedException(responseError) else data }
}
