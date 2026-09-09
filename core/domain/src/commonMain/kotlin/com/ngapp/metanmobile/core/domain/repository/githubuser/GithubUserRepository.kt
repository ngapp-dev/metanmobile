package com.ngapp.metanmobile.core.domain.repository.githubuser

import com.ngapp.metanmobile.core.domain.sync.Syncable
import com.ngapp.metanmobile.core.model.githubuser.GithubUserResource
import kotlinx.coroutines.flow.Flow

interface GithubUserRepository : Syncable {
    fun getGithubUser(): Flow<GithubUserResource?>
}
