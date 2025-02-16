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

package com.ngapp.metanmobile.feature.favorites.state

import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig

sealed interface FavoritesAction {
    data class UpdateLocation(val hasPermissions: Boolean) : FavoritesAction
    data class ShowAlertDialog(val showDialog: Boolean) : FavoritesAction
    data class ShowBottomSheet(val showBottomSheet: Boolean) : FavoritesAction
    data class UpdateSearchQuery(val input: String) : FavoritesAction
    data class UpdateSortingConfig(val stationSortingConfig: StationSortingConfig) : FavoritesAction
    data class UpdateStationFavorite(val stationCode: String, val favorite: Boolean) : FavoritesAction
    data class UpdateStationForDelete(val station: UserStationResource) : FavoritesAction
    data class UpdateStationCode(val stationCode: String) : FavoritesAction
}