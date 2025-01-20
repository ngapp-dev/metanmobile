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

package com.ngapp.metanmobile.feature.stations.detail.newdetail.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.stations.StationBusyHours
import com.ngapp.metanmobile.core.ui.stations.StationInfoRow
import com.ngapp.metanmobile.core.ui.stations.StationPhonesRow
import com.ngapp.metanmobile.core.ui.stations.StationWorkTimeRow

@Composable
internal fun StationDetailOverview(
    address: String,
    coordinates: String,
    workingTime: String,
    phones: String,
    station: UserStationResource,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        StationInfoRow(
            rowIcon = MMIcons.LocationOnOutlined,
            text = address,
            isExpandable = false,
        )
        StationInfoRow(
            rowIcon = MMIcons.ExploreOutlined,
            text = coordinates,
            isExpandable = false,
        )
        StationWorkTimeRow(
            rowIcon = MMIcons.ClockOutlined,
            workingTime = workingTime,
        )
        StationPhonesRow(
            rowIcon = MMIcons.CallFilled,
            phones = phones,
        )
        StationBusyHours(station = station)
    }
}

@Composable
internal fun StationDetailUpdates(modifier: Modifier = Modifier) {

}

@Composable
internal fun StationDetailPayments(modifier: Modifier = Modifier) {

}

@Composable
internal fun StationDetailPhotos(modifier: Modifier = Modifier) {

}