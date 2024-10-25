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

package com.ngapp.metanmobile.core.database.model.career

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngapp.metanmobile.core.model.career.CareerResource

@Entity(tableName = "career_resources")
data class CareerResourceEntity(
    @PrimaryKey
    val id: String,
    val code: String,
    @ColumnInfo(name = "preview_picture") val previewPicture: String,
    @ColumnInfo(name = "detail_picture") val detailPicture: String,
    @ColumnInfo(name = "is_active") val isActive: Int,
    val title: String,
    @ColumnInfo(name = "date_created") val dateCreated: Long,
    val exp: String,
    val place: String,
    val description: String,
    val requirements: String,
    val responsibilities: String,
    val number: Int,
)

fun CareerResourceEntity.asExternalModel() = CareerResource(
    id = id,
    code = code,
    previewPicture = previewPicture,
    detailPicture = detailPicture,
    isActive = isActive,
    title = title,
    dateCreated = dateCreated,
    exp = exp,
    place = place,
    description = description,
    requirements = requirements,
    responsibilities = responsibilities,
    number = number
)