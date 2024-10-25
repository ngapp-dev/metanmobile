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

package com.ngapp.metanmobile.core.testing.data.station

import com.ngapp.metanmobile.core.model.station.StationResource
import kotlinx.datetime.Clock

val stationResourcesTestData: List<StationResource> = listOf(
    StationResource(
        id = "1",
        code = "ST001",
        previewPicture = "https://metan.by/upload/iblock/3ef/3ef51481d6a91f3f0a55220951f66c7a.JPG",
        detailPicture = "https://metan.by/upload/iblock/177/177b4fa50577a75d614bbbf46633ddfb.jpg",
        isActive = 1,
        isOperate = 1,
        type = "АГНКС",
        address = "123 Main St, City A",
        region = "Region A",
        phones = "+1234567890",
        service = "Full service",
        workingTime = "24/7",
        payment = "Cash, Card",
        latitude = "40.712776",
        longitude = "-74.005974",
        busyOnMonday = "23,30,10,8,2,15,21,15,39,30,33,28,25,29,21,21,31,33,37,22,26,26,25,26",
        busyOnTuesday = "16,9,9,9,11,14,25,10,35,36,33,30,18,20,29,31,28,29,35,20,31,28,20,27",
        busyOnWednesday = "22,12,6,10,11,14,28,12,36,33,25,22,20,19,17,24,32,23,31,19,31,31,27,23",
        busyOnThursday = "24,16,9,2,6,18,27,17,36,35,31,35,26,23,24,21,28,29,31,22,32,34,25,27",
        busyOnFriday = "20,19,10,7,6,17,31,15,34,32,28,22,28,27,32,27,34,35,35,18,33,33,22,28",
        busyOnSaturday = "25,31,19,17,13,19,33,16,34,35,30,18,31,24,21,26,34,39,36,21,33,37,23,25",
        busyOnSunday = "26,33,25,23,12,15,25,12,35,35,30,26,25,25,23,32,29,24,36,19,32,31,25,21",
        googleTag = "tag1",
        googleMapsTag = "mapsTag1",
        yandexTag = "yandexTag1",
        title = "Main Station",
        dateCreated = Clock.System.now().toEpochMilliseconds(),
        url = "https://metan.by/ecogas-map/agnks_baranovichi/"
    ),
    StationResource(
        id = "2",
        code = "ST002",
        previewPicture = "https://metan.by/upload/iblock/3ef/3ef51481d6a91f3f0a55220951f66c7a.JPG",
        detailPicture = "https://example.com/detail2.jpg",
        isActive = 1,
        isOperate = 1,
        type = "АГНКС",
        address = "456 Oak St, City B",
        region = "Region B",
        phones = "+0987654321",
        service = "Self-service",
        workingTime = "6am - 10pm",
        payment = "Card",
        latitude = "34.052235",
        longitude = "-118.243683",
        busyOnMonday = "23,30,10,8,2,15,21,15,39,30,33,28,25,29,21,21,31,33,37,22,26,26,25,26",
        busyOnTuesday = "16,9,9,9,11,14,25,10,35,36,33,30,18,20,29,31,28,29,35,20,31,28,20,27",
        busyOnWednesday = "22,12,6,10,11,14,28,12,36,33,25,22,20,19,17,24,32,23,31,19,31,31,27,23",
        busyOnThursday = "24,16,9,2,6,18,27,17,36,35,31,35,26,23,24,21,28,29,31,22,32,34,25,27",
        busyOnFriday = "20,19,10,7,6,17,31,15,34,32,28,22,28,27,32,27,34,35,35,18,33,33,22,28",
        busyOnSaturday = "25,31,19,17,13,19,33,16,34,35,30,18,31,24,21,26,34,39,36,21,33,37,23,25",
        busyOnSunday = "26,33,25,23,12,15,25,12,35,35,30,26,25,25,23,32,29,24,36,19,32,31,25,21",
        googleTag = "tag2",
        googleMapsTag = "mapsTag2",
        yandexTag = "yandexTag2",
        title = "Oak Electric Station",
        dateCreated = Clock.System.now().toEpochMilliseconds(),
        url = "https://metan.by/ecogas-map/agnks_baranovichi/"
    ),
    StationResource(
        id = "3",
        code = "ST003",
        previewPicture = "https://metan.by/upload/iblock/3ef/3ef51481d6a91f3f0a55220951f66c7a.JPG",
        detailPicture = "https://metan.by/upload/iblock/177/177b4fa50577a75d614bbbf46633ddfb.jpg",
        isActive = 1,
        isOperate = 1,
        type = "АГНКС",
        address = "789 Pine St, City C",
        region = "Region C",
        phones = "+1122334455",
        service = "Full service",
        workingTime = "7am - 11pm",
        payment = "Cash, Card, Mobile",
        latitude = "51.507351",
        longitude = "-0.127758",
        busyOnMonday = "23,30,10,8,2,15,21,15,39,30,33,28,25,29,21,21,31,33,37,22,26,26,25,26",
        busyOnTuesday = "16,9,9,9,11,14,25,10,35,36,33,30,18,20,29,31,28,29,35,20,31,28,20,27",
        busyOnWednesday = "22,12,6,10,11,14,28,12,36,33,25,22,20,19,17,24,32,23,31,19,31,31,27,23",
        busyOnThursday = "24,16,9,2,6,18,27,17,36,35,31,35,26,23,24,21,28,29,31,22,32,34,25,27",
        busyOnFriday = "20,19,10,7,6,17,31,15,34,32,28,22,28,27,32,27,34,35,35,18,33,33,22,28",
        busyOnSaturday = "25,31,19,17,13,19,33,16,34,35,30,18,31,24,21,26,34,39,36,21,33,37,23,25",
        busyOnSunday = "26,33,25,23,12,15,25,12,35,35,30,26,25,25,23,32,29,24,36,19,32,31,25,21",
        googleTag = "tag3",
        googleMapsTag = "mapsTag3",
        yandexTag = "yandexTag3",
        title = "Pine Hybrid Station",
        dateCreated = Clock.System.now().toEpochMilliseconds(),
        url = "https://metan.by/ecogas-map/agnks_baranovichi/"
    ),
    StationResource(
        id = "4",
        code = "ST004",
        previewPicture = "https://metan.by/upload/iblock/3ef/3ef51481d6a91f3f0a55220951f66c7a.JPG",
        detailPicture = "https://metan.by/upload/iblock/177/177b4fa50577a75d614bbbf46633ddfb.jpg",
        isActive = 1,
        isOperate = 1,
        type = "АГНКС",
        address = "101 Maple St, City D",
        region = "Region D",
        phones = "+2233445566",
        service = "Self-service",
        workingTime = "5am - 12am",
        payment = "Card",
        latitude = "35.689487",
        longitude = "139.691711",
        busyOnMonday = "23,30,10,8,2,15,21,15,39,30,33,28,25,29,21,21,31,33,37,22,26,26,25,26",
        busyOnTuesday = "16,9,9,9,11,14,25,10,35,36,33,30,18,20,29,31,28,29,35,20,31,28,20,27",
        busyOnWednesday = "22,12,6,10,11,14,28,12,36,33,25,22,20,19,17,24,32,23,31,19,31,31,27,23",
        busyOnThursday = "24,16,9,2,6,18,27,17,36,35,31,35,26,23,24,21,28,29,31,22,32,34,25,27",
        busyOnFriday = "20,19,10,7,6,17,31,15,34,32,28,22,28,27,32,27,34,35,35,18,33,33,22,28",
        busyOnSaturday = "25,31,19,17,13,19,33,16,34,35,30,18,31,24,21,26,34,39,36,21,33,37,23,25",
        busyOnSunday = "26,33,25,23,12,15,25,12,35,35,30,26,25,25,23,32,29,24,36,19,32,31,25,21",
        googleTag = "tag4",
        googleMapsTag = "mapsTag4",
        yandexTag = "yandexTag4",
        title = "Maple Station",
        dateCreated = Clock.System.now().toEpochMilliseconds(),
        url = "https://metan.by/ecogas-map/agnks_baranovichi/"
    )
)