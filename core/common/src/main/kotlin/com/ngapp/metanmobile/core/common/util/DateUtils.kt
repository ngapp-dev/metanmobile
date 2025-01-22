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

package com.ngapp.metanmobile.core.common.util

import kotlinx.datetime.Clock
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun formatRssDate(dateString: String?): Long {
    val cleanedDateString = dateString?.replace(Regex("^[a-zA-Z]+, "), "")
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    val zonedDateTime = ZonedDateTime.parse(cleanedDateString, formatter)
    return zonedDateTime.toInstant().toEpochMilli()
}

fun shortFormatUnixDataToString(unixDate: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val dated = Date(unixDate * 1000)
    return sdf.format(dated)
}

fun fromStringToListFloat(stringListString: String): List<Float?> {
    return if (stringListString.isNotEmpty()) {
        stringListString.split(",").map { it.toFloatOrNull() }
    } else {
        emptyList()
    }
}

fun isNewsNew(dateCreated: Long, thresholdDays: Int = 10): Boolean {
    val now = Clock.System.now().toEpochMilliseconds()
    val diffInMillis = now - dateCreated
    val diffInDays = diffInMillis / 86400000
    return diffInDays < thresholdDays
}
