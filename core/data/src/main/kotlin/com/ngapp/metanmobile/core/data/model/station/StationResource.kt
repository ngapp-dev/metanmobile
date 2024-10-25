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

package com.ngapp.metanmobile.core.data.model.station

import com.ngapp.metanmobile.core.common.util.formatRssDate
import com.ngapp.metanmobile.core.database.model.station.StationResourceEntity
import com.ngapp.metanmobile.core.network.model.station.NetworkStationResource

fun NetworkStationResource.asEntity() = StationResourceEntity(
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
    dateCreated = formatRssDate(dateCreated),
    url = url
)