package com.ngapp.metanmobile.core.network.network

import com.ngapp.metanmobile.core.network.GithubNetworkDataSource
import com.ngapp.metanmobile.core.network.client.GithubHttpDataSource
import com.ngapp.metanmobile.core.network.model.githubuser.NetworkGithubUserResource

class KtorGithubNetwork(private val source: GithubHttpDataSource) : GithubNetworkDataSource {
    override suspend fun getGithubUser() = source.getNgappDevUser().let {
        NetworkGithubUserResource(it.login, it.id, it.avatarUrl, it.url, it.htmlUrl, it.name, it.company, it.blog, it.location, it.email, it.bio, it.twitterUsername)
    }
}
