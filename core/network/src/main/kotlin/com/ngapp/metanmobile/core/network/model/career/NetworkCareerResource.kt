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

package com.ngapp.metanmobile.core.network.model.career

import com.prof.rssparser.Article

data class NetworkCareerResource(
    val id: String,
    val code: String = "",
    val previewPicture: String = "",
    val detailPicture: String = "",
    val isActive: Int = 1,
    val title: String = "",
    val dateCreated: String = "",
    val exp: String = "",
    val place: String = "",
    val description: String = "",
    val requirements: String = "",
    val responsibilities: String = "",
    val number: Int = 1,
)

fun Article.asNetworkCareerResource() = NetworkCareerResource(
    id = categories[CareerCategoryValues.CATEGORY_ID],
    code = categories[CareerCategoryValues.CATEGORY_CODE],
    previewPicture = categories[CareerCategoryValues.CATEGORY_PREVIEW_PICTURE],
    detailPicture = categories[CareerCategoryValues.CATEGORY_DETAIL_PICTURE],
    isActive = if (categories[CareerCategoryValues.CATEGORY_ACTIVE] == "active") 1 else 0,
    title = title ?: "",
    dateCreated = pubDate ?: "",
    exp = categories[CareerCategoryValues.CATEGORY_EXP],
    place = categories[CareerCategoryValues.CATEGORY_PLACE],
    description = categories[CareerCategoryValues.CATEGORY_DESCRIPTION],
    requirements = categories[CareerCategoryValues.CATEGORY_REQ],
    responsibilities = categories[CareerCategoryValues.CATEGORY_RESP],
    number = categories[CareerCategoryValues.CATEGORY_NUMBER].toInt(),
)

