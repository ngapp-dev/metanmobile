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

package com.ngapp.metanmobile.core.datastore

import androidx.datastore.core.DataMigration

/**
 * Migrates from using lists to maps for user data.
 */
internal object ListToMapMigration : DataMigration<UserPreferences> {

    override suspend fun cleanUp() = Unit

    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // Migrate favorites
            favoriteStationResourceCodes.clear()
            favoriteStationResourceCodes.putAll(
                currentData.deprecatedFavoriteStationResourceCodesList.associateWith { true },
            )
            deprecatedFavoriteStationResourceCodes.clear()

            // Mark migration as complete
            hasDoneListToMapMigration = true
        }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        !currentData.hasDoneListToMapMigration
}
