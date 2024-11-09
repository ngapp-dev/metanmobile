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

package com.ngapp.metanmobile.core.network.model.news

import com.prof18.rssparser.model.RssItem

data class NetworkNewsResource(
    val id: String,
    val code: String = "",
    val isPinned: Int = 0,
    val previewPicture: String = "",
    val detailPicture: String = "",
    val isActive: Int = 1,
    val isOperate: Int = 1,
    val relatedStation: String = "",
    val title: String = "",
    val dateCreated: String = "",
    val description: String = "",
    val content: String = "",
    val url: String = "",
)

fun RssItem.asNetworkNewsResource(): NetworkNewsResource {
    val contentImage =
        this.content?.substringAfter("src=\"")?.substringBefore(".jpg")

    return NetworkNewsResource(
        id = categories[NewsCategoryValues.CATEGORY_ID],
        code = categories[NewsCategoryValues.CATEGORY_CODE],
        isPinned = if (categories[NewsCategoryValues.CATEGORY_PINNED] == "pinned") 1 else 0,
        previewPicture = categories[NewsCategoryValues.CATEGORY_PREVIEW_PICTURE],
        detailPicture = categories[NewsCategoryValues.CATEGORY_DETAIL_PICTURE].ifEmpty { "https://metan.by$contentImage.jpg" },
        isActive = if (categories[NewsCategoryValues.CATEGORY_ACTIVE] == "active") 1 else 0,
        isOperate = if (categories[NewsCategoryValues.CATEGORY_OPERATE] == "Работает") 1 else 0,
        relatedStation = categories[NewsCategoryValues.CATEGORY_LABEL_NEWS],
        title = title ?: "",
        dateCreated = pubDate ?: "",
        description = description ?: "",
        content = content ?: "",
        url = link ?: "",
    )
}

