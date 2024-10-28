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

package com.ngapp.metanmobile.core.datastore.model

import com.ngapp.metanmobile.core.datastore.StationSortingConfigProto
import com.ngapp.metanmobile.core.datastore.StationTypeProto
import com.ngapp.metanmobile.core.datastore.StationsSortingTypeProto
import com.ngapp.metanmobile.core.model.station.StationType
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingType

fun StationSortingConfigProto.toModel() = StationSortingConfig(
    sortingType = this.sortTypeConfig.toModel(),
    sortingOrder = this.sortOrderConfig.toModel(),
    activeStationTypes = this.activeStationTypesConfigList.map { it.toModel() }
)

fun StationsSortingTypeProto.toModel(): StationSortingType {
    return when (this) {
        StationsSortingTypeProto.UNRECOGNIZED -> StationSortingType.STATION_NAME
        StationsSortingTypeProto.DISTANCE -> StationSortingType.STATION_NAME
        StationsSortingTypeProto.STATION_NAME -> StationSortingType.STATION_NAME
    }
}

fun StationTypeProto.toModel(): StationType {
    return when (this) {
        StationTypeProto.UNRECOGNIZED -> StationType.CNG
        StationTypeProto.CLFS -> StationType.CLFS
        StationTypeProto.CNG -> StationType.CNG
        StationTypeProto.SERVICE -> StationType.SERVICE
    }
}

fun StationSortingType.toProto(): StationsSortingTypeProto {
    return when (this) {
//        StationSortingType.DISTANCE -> StationsSortingTypeProto.DISTANCE
        StationSortingType.STATION_NAME -> StationsSortingTypeProto.STATION_NAME
    }
}

fun StationType.toProto(): StationTypeProto {
    return when (this) {
        StationType.CLFS -> StationTypeProto.CLFS
        StationType.CNG -> StationTypeProto.CNG
        StationType.SERVICE -> StationTypeProto.SERVICE
    }
}
