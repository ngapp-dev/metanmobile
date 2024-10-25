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

package com.ngapp.metanmobile.core.model.news

import com.ngapp.metanmobile.core.model.userdata.UserData
import kotlinx.datetime.Clock

data class UserNewsResource(
    val id: String,
    val code: String,
    val isPinned: Int,
    val previewPicture: String,
    val detailPicture: String,
    val isActive: Int,
    val isOperate: Int,
    val relatedStation: String,
    val title: String,
    val dateCreated: Long,
    val description: String,
    val content: String,
    val url: String,
    val isSearchable: Int,
    val hasBeenViewed: Boolean,
) {
    companion object {
        fun init() = UserNewsResource(
            id = "1",
            code = "agnks_grodno2",
            isPinned = 1,
            previewPicture = "",
            detailPicture = "",
            isActive = 1,
            isOperate = 1,
            relatedStation = "",
            title = "АГНКС Гродно-2",
            dateCreated = Clock.System.now().toEpochMilliseconds(),
            description = "",
            content = "",
            url = "",
            isSearchable = 1,
            hasBeenViewed = false
        )
    }

    constructor(newsResource: NewsResource, userData: UserData) : this(
        id = newsResource.id,
        code = newsResource.code,
        isPinned = newsResource.isPinned,
        previewPicture = newsResource.previewPicture,
        detailPicture = newsResource.detailPicture,
        isActive = newsResource.isActive,
        isOperate = newsResource.isOperate,
        relatedStation = newsResource.relatedStation,
        title = newsResource.title,
        dateCreated = newsResource.dateCreated,
        description = newsResource.description,
        content = newsResource.content,
        url = newsResource.url,
        isSearchable = newsResource.isSearchable,
        hasBeenViewed = newsResource.id in userData.viewedNewsResources
    )
}

fun List<NewsResource>.mapToUserNewsResources(userData: UserData): List<UserNewsResource> =
    map { UserNewsResource(it, userData) }
