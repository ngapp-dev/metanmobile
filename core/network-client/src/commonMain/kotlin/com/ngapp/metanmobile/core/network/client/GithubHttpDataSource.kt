package com.ngapp.metanmobile.core.network.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class GithubHttpDataSource(
    private val client: MetanEcogasHttpClient,
) {
    suspend fun getNgappDevUser(): GithubUserDto = client.get("users/ngapp-dev")
}

@Serializable
data class GithubUserDto(
    val login: String? = "",
    val id: Int = 0,
    @SerialName("avatar_url") val avatarUrl: String? = "",
    val url: String? = "",
    @SerialName("html_url") val htmlUrl: String? = "",
    val name: String? = "",
    val company: String? = "",
    val blog: String? = "",
    val location: String = "",
    val email: String? = "",
    val bio: String? = "",
    @SerialName("twitter_username") val twitterUsername: String? = "",
)
