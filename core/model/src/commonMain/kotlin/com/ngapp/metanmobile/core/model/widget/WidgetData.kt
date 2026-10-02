/*
 * Copyright 2026 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.core.model.widget

import com.ngapp.metanmobile.core.model.location.LocationResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource

/**
 * What the home-screen widgets show, all of it already stored locally by the app.
 *
 * [stations] carry their distance to [location] (null while the location is unknown) and the
 * user's favorite flag, same as everywhere else in the app.
 */
data class WidgetData(
    val cngPrice: PriceResource?,
    val location: LocationResource?,
    val stations: List<UserStationResource>,
)
