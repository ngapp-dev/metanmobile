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

package com.ngapp.metanmobile.feature.stations.list.state

import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig

sealed interface StationsAction {
    data class UpdateLocation(val hasPermissions: Boolean) : StationsAction
    data class ShowAlertDialog(val showDialog: Boolean) : StationsAction
    data class UpdateSearchQuery(val input: String) : StationsAction
    data class UpdateSortingConfig(val stationSortingConfig: StationSortingConfig) : StationsAction
    data class UpdateStationCode(val stationCode: String) : StationsAction
    data class UpdateStationFavorite(val stationCode: String, val favorite: Boolean) : StationsAction
}