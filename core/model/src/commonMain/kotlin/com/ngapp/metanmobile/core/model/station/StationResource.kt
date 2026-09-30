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

package com.ngapp.metanmobile.core.model.station

import kotlinx.datetime.Clock

data class StationResource(
    val id: String,
    val code: String,
    val previewPicture: String,
    val detailPicture: String,
    val isActive: Int,
    val isOperate: Int,
    val type: String,
    val address: String,
    val region: String,
    val phones: String,
    val service: String,
    val workingTime: String,
    val payment: String,
    val latitude: String,
    val longitude: String,
    val busyOnMonday: String,
    val busyOnTuesday: String,
    val busyOnWednesday: String,
    val busyOnThursday: String,
    val busyOnFriday: String,
    val busyOnSaturday: String,
    val busyOnSunday: String,
    val googleTag: String,
    val googleMapsTag: String,
    val yandexTag: String,
    val title: String,
    val dateCreated: Long,
    val url: String,
) {
    companion object {
        fun init() = StationResource(
            id = "1",
            code = "agnks_grodno2",
            previewPicture = "",
            detailPicture = "",
            isActive = 1,
            isOperate = 1,
            type = "АГНКС",
            address = "",
            region = "",
            phones = "",
            service = "",
            workingTime = "",
            payment = "",
            latitude = "53.925653",
            longitude = "27.667294",
            busyOnMonday = "",
            busyOnTuesday = "",
            busyOnWednesday = "",
            busyOnThursday = "",
            busyOnFriday = "",
            busyOnSaturday = "",
            busyOnSunday = "",
            googleTag = "",
            googleMapsTag = "",
            yandexTag = "",
            title = "АГНКС Гродно-2",
            dateCreated = Clock.System.now().toEpochMilliseconds(),
            url = "",
        )
    }
}