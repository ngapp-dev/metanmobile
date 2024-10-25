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

package com.ngapp.metanmobile.core.database.util

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class InstantConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}

class ListStringConverter {
    private val json = Json { encodeDefaults = true }

    @TypeConverter
    fun toListOfStrings(stringValue: String): List<String>? {
        return try {
            json.decodeFromString<List<String>>(stringValue)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun fromListOfStrings(listOfString: List<String>?): String {
        return json.encodeToString(listOfString ?: emptyList())
    }
}


