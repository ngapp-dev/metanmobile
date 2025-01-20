/*
 * Copyright 2025 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.core.network.model.station

import com.prof18.rssparser.model.RssItem

data class NetworkStationResource(
    val id: String,
    val code: String = "",
    val previewPicture: String = "",
    val detailPicture: String = "",
    val isActive: Int = 1,
    val isOperate: Int = 1,
    val type: String = "",
    val address: String = "",
    val region: String = "",
    val phones: String = "",
    val service: String = "",
    val workingTime: String = "",
    val payment: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val busyOnMonday: String = "",
    val busyOnTuesday: String = "",
    val busyOnWednesday: String = "",
    val busyOnThursday: String = "",
    val busyOnFriday: String = "",
    val busyOnSaturday: String = "",
    val busyOnSunday: String = "",
    val googleTag: String = "",
    val googleMapsTag: String = "",
    val yandexTag: String = "",
    val title: String = "",
    val dateCreated: String = "",
    val url: String = "",
)

fun RssItem.asNetworkStationResource() = NetworkStationResource(
    id = categories[StationCategoryValues.CATEGORY_ID],
    code = categories[StationCategoryValues.CATEGORY_CODE],
    previewPicture = categories[StationCategoryValues.CATEGORY_PREVIEW_PICTURE],
    detailPicture = categories[StationCategoryValues.CATEGORY_DETAIL_PICTURE],
    isActive = if (categories[StationCategoryValues.CATEGORY_ACTIVE] == "active") 1 else 0,
    isOperate = if (categories[StationCategoryValues.CATEGORY_OPERATE] == "Работает") 1 else 0,
    type = categories[StationCategoryValues.CATEGORY_TYPE],
    address = categories[StationCategoryValues.CATEGORY_ADDRESS],
    region = categories[StationCategoryValues.CATEGORY_REGION],
    phones = categories[StationCategoryValues.CATEGORY_PHONE],
    service = categories[StationCategoryValues.CATEGORY_SERVICE],
    workingTime = categories[StationCategoryValues.CATEGORY_WORKING_TIME],
    payment = categories[StationCategoryValues.CATEGORY_PAYMENT],
    latitude = categories[StationCategoryValues.CATEGORY_COORDINATE_TAGS].substringAfter(","),
    longitude = categories[StationCategoryValues.CATEGORY_COORDINATE_TAGS].substringBefore(","),
    busyOnMonday = categories[StationCategoryValues.CATEGORY_MONDAY],
    busyOnTuesday = categories[StationCategoryValues.CATEGORY_TUESDAY],
    busyOnWednesday = categories[StationCategoryValues.CATEGORY_WEDNESDAY],
    busyOnThursday = categories[StationCategoryValues.CATEGORY_THURSDAY],
    busyOnFriday = categories[StationCategoryValues.CATEGORY_FRIDAY],
    busyOnSaturday = categories[StationCategoryValues.CATEGORY_SATURDAY],
    busyOnSunday = categories[StationCategoryValues.CATEGORY_SUNDAY],
    googleTag = categories[StationCategoryValues.CATEGORY_GOOGLE_TAG],
    googleMapsTag = categories[StationCategoryValues.CATEGORY_GOOGLE_MAPS_TAG],
    yandexTag = categories[StationCategoryValues.CATEGORY_YANDEX_TAG],
    title = title ?: "",
    dateCreated = pubDate ?: "",
    url = link ?: ""
)

