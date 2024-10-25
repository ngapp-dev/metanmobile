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

package com.ngapp.metanmobile.core.database.model.station

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngapp.metanmobile.core.model.station.StationResource

@Entity(tableName = "station_resources")
data class StationResourceEntity(
    @PrimaryKey
    val id: String,
    val code: String,
    @ColumnInfo(name = "preview_picture") val previewPicture: String,
    @ColumnInfo(name = "detail_picture") val detailPicture: String,
    @ColumnInfo(name = "is_active") val isActive: Int,
    @ColumnInfo(name = "is_operate") val isOperate: Int,
    val type: String,
    val address: String,
    val region: String,
    val phones: String,
    val service: String,
    @ColumnInfo(name = "working_time") val workingTime: String,
    val payment: String,
    val latitude: String,
    val longitude: String,
    @ColumnInfo(name = "distance_between") val distanceBetween: Double = 0.0,
    @ColumnInfo(name = "busy_on_monday") val busyOnMonday: String,
    @ColumnInfo(name = "busy_on_tuesday") val busyOnTuesday: String,
    @ColumnInfo(name = "busy_on_wednesday") val busyOnWednesday: String,
    @ColumnInfo(name = "busy_on_thursday") val busyOnThursday: String,
    @ColumnInfo(name = "busy_on_friday") val busyOnFriday: String,
    @ColumnInfo(name = "busy_on_saturday") val busyOnSaturday: String,
    @ColumnInfo(name = "busy_on_sunday") val busyOnSunday: String,
    @ColumnInfo(name = "google_tag") val googleTag: String,
    @ColumnInfo(name = "google_maps_tag") val googleMapsTag: String,
    @ColumnInfo(name = "yandex_tag") val yandexTag: String,
    val title: String,
    @ColumnInfo(name = "date_created") val dateCreated: Long,
    val url: String,
)

fun StationResourceEntity.asExternalModel() = StationResource(
    id = id,
    code = code,
    previewPicture = previewPicture,
    detailPicture = detailPicture,
    isActive = isActive,
    isOperate = isOperate,
    type = type,
    address = address,
    region = region,
    phones = phones,
    service = service,
    workingTime = workingTime,
    payment = payment,
    latitude = latitude,
    longitude = longitude,
    busyOnMonday = busyOnMonday,
    busyOnTuesday = busyOnTuesday,
    busyOnWednesday = busyOnWednesday,
    busyOnThursday = busyOnThursday,
    busyOnFriday = busyOnFriday,
    busyOnSaturday = busyOnSaturday,
    busyOnSunday = busyOnSunday,
    googleTag = googleTag,
    googleMapsTag = googleMapsTag,
    yandexTag = yandexTag,
    title = title,
    dateCreated = dateCreated,
    url = url,
)