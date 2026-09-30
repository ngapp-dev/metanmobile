package com.ngapp.metanmobile.core.network.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

/** Shared Cloudflare API transport for Android and iOS. */
class MetanEcogasHttpClient(
    @PublishedApi internal val client: HttpClient = createMetanEcogasHttpClient(),
    baseUrl: String,
) {
    @PublishedApi internal val baseUrl = baseUrl.trimEnd('/') + "/"

    suspend inline fun <reified T> get(path: String): T =
        client.get(baseUrl + path.trimStart('/')).body()

    fun close() = client.close()
}

@Serializable
data class CloudflareResponse<T>(
    val data: T,
    val error: String? = null,
)

expect fun createMetanEcogasHttpClient(): HttpClient
