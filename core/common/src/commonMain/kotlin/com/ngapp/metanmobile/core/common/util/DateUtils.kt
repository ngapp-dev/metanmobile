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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

private val rssDateRegex = Regex(
    """(?:[A-Za-z]+,\s*)?(\d{1,2})\s+([A-Za-z]{3})\s+(\d{4})\s+(\d{2}):(\d{2}):(\d{2})\s+([+-]\d{4})""",
)

private val monthNumbers = mapOf(
    "Jan" to 1, "Feb" to 2, "Mar" to 3, "Apr" to 4, "May" to 5, "Jun" to 6,
    "Jul" to 7, "Aug" to 8, "Sep" to 9, "Oct" to 10, "Nov" to 11, "Dec" to 12,
)

/** Converts the RFC 822-style timestamp preserved by the Cloudflare API to epoch milliseconds. */
fun formatRssDate(dateString: String?): Long {
    val groups = requireNotNull(dateString?.let(rssDateRegex::matchEntire)) { "Invalid API date: $dateString" }.groupValues
    val timeZoneOffset = groups[7].let { "${it.take(3)}:${it.takeLast(2)}" }
    return LocalDateTime(
        year = groups[3].toInt(),
        monthNumber = requireNotNull(monthNumbers[groups[2]]),
        dayOfMonth = groups[1].toInt(),
        hour = groups[4].toInt(),
        minute = groups[5].toInt(),
        second = groups[6].toInt(),
    ).toInstant(TimeZone.of(timeZoneOffset)).toEpochMilliseconds()
}

fun shortFormatUnixDataToString(unixDate: Long): String {
    val date = Instant.fromEpochSeconds(unixDate).toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${date.dayOfMonth.toString().padStart(2, '0')}." +
        "${date.monthNumber.toString().padStart(2, '0')}.${date.year.toString().padStart(4, '0')}"
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
