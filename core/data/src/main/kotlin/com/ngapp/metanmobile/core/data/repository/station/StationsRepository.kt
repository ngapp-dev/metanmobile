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

package com.ngapp.metanmobile.core.data.repository.station

import com.ngapp.metanmobile.core.domain.repository.station.StationResourceQuery as DomainStationResourceQuery
import com.ngapp.metanmobile.core.domain.repository.station.StationsRepository as DomainStationsRepository

/**
 * Compatibility aliases while callers move from the Android-only data package to the shared
 * domain contract. One contract keeps Koin resolution identical on Android and iOS.
 */
typealias StationResourceQuery = DomainStationResourceQuery
typealias StationsRepository = DomainStationsRepository
