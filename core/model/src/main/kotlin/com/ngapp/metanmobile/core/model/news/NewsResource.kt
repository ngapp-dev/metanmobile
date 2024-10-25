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

import kotlinx.datetime.Clock.System

data class NewsResource(
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
) {
    companion object {
        fun init() = NewsResource(
            id = "1",
            code = "",
            isPinned = 0,
            previewPicture = "",
            detailPicture = "",
            isActive = 1,
            isOperate = 1,
            relatedStation = "",
            title = "Не возможно загрузить новые компрессоры на АГНКС Гродно-2",
            dateCreated = System.now().toEpochMilliseconds(),
            description = "Lorem Ipsum",
            content = "Lorem Ipsum Lorem Ipsum Lorem Ipsum",
            url = "",
            isSearchable = 1,
        )
    }
}