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

package com.ngapp.metanmobile.core.database.model.news

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngapp.metanmobile.core.model.news.NewsResource

@Entity(tableName = "news_resources")
data class NewsResourceEntity(
    @PrimaryKey
    val id: String,
    val code: String,
    @ColumnInfo(name = "is_pinned") val isPinned: Int,
    @ColumnInfo(name = "preview_picture") val previewPicture: String,
    @ColumnInfo(name = "detail_picture") val detailPicture: String,
    @ColumnInfo(name = "is_active") val isActive: Int,
    @ColumnInfo(name = "is_operate") val isOperate: Int,
    @ColumnInfo(name = "related_station") val relatedStation: String,
    val title: String,
    @ColumnInfo(name = "date_created")val dateCreated: Long,
    val description: String,
    val content: String,
    val url: String,
    @ColumnInfo(name = "is_searchable") val isSearchable: Int,
)

fun NewsResourceEntity.asExternalModel() = NewsResource(
    id = id,
    code = code,
    isPinned = isPinned,
    previewPicture = previewPicture,
    detailPicture = detailPicture,
    isActive = isActive,
    isOperate = isOperate,
    relatedStation = relatedStation,
    title = title,
    dateCreated = dateCreated,
    description = description,
    content = content,
    url = url,
    isSearchable = isSearchable
)
