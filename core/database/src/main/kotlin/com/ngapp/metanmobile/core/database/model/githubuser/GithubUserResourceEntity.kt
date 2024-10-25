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

package com.ngapp.metanmobile.core.database.model.githubuser

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngapp.metanmobile.core.model.githubuser.GithubUserResource

@Entity(tableName = "github_user_resources")
data class GithubUserResourceEntity(
    @PrimaryKey
    val id: Int,
    val login: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    val url: String,
    @ColumnInfo(name = "html_url") val htmlUrl: String,
    val name: String,
    val company: String,
    val blog: String,
    val location: String,
    val email: String,
    val bio: String,
    @ColumnInfo(name = "twitter_username") val twitterUsername: String,
)

fun GithubUserResourceEntity.asExternalModel() = GithubUserResource(
    id = id,
    login = login,
    avatarUrl = avatarUrl,
    url = url,
    htmlUrl = htmlUrl,
    name = name,
    company = company,
    blog = blog,
    location = location,
    email = email,
    bio = bio,
    twitterUsername = twitterUsername
)
