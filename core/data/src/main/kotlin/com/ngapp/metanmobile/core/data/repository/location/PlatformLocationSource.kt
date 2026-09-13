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

package com.ngapp.metanmobile.core.data.repository.location

/** A single location fix, decoupled from any platform-specific location API. */
data class PlatformLocationPoint(
    val latitude: Double,
    val longitude: Double,
    val time: Long,
)

/**
 * Whether the platform's location stack is currently usable (Google Play Services on Android;
 * always `true` on iOS, which has no equivalent gate on top of CoreLocation).
 */
expect fun isPlatformLocationAvailable(): Boolean

/** One-shot platform location fetch, used by [OfflineFirstLocationsRepository]. */
expect class PlatformLocationSource() {
    suspend fun getCurrentLocation(): PlatformLocationPoint?
}
