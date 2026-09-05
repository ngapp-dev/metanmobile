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
import org.junit.Test
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DateUtilsTest {

    @Test
    fun `formatRssDate parses an RSS-style date, dropping the weekday prefix`() {
        val millis = formatRssDate("Tue, 02 Jan 2024 15:04:05 +0000")

        assertEquals(1704207845000L, millis)
    }

    @Test
    fun `formatRssDate parses correctly with no weekday prefix too`() {
        val millis = formatRssDate("02 Jan 2024 15:04:05 +0000")

        assertEquals(1704207845000L, millis)
    }

    @Test
    fun `formatRssDate throws on a null or unparsable date`() {
        assertFailsWith<Exception> { formatRssDate(null) }
        assertFailsWith<Exception> { formatRssDate("not a date") }
    }

    @Test
    fun `shortFormatUnixDataToString formats a unix-seconds timestamp as dd-MM-yyyy`() {
        // Noon UTC keeps the calendar date stable across the timezones a dev/CI box realistically
        // runs in, since shortFormatUnixDataToString formats using the JVM's default timezone.
        val noonUtcJune15_2024 = 1718452800L // 2024-06-15T12:00:00Z, in seconds
        val defaultTimeZone = TimeZone.getDefault()
        val offsetHours = defaultTimeZone.getOffset(noonUtcJune15_2024 * 1000).toDouble() / 3_600_000
        // Skip on the (unusual, real-device-only) timezones where noon UTC crosses into the
        // adjacent calendar day, rather than asserting a result that would legitimately vary.
        if (offsetHours <= -12.0 || offsetHours >= 12.0) return

        val formatted = shortFormatUnixDataToString(noonUtcJune15_2024)

        assertEquals("15.06.2024", formatted)
    }

    @Test
    fun `fromStringToListFloat parses a comma-separated list, keeping unparsable entries as null`() {
        assertEquals(listOf(1.5f, 2f, 3.25f), fromStringToListFloat("1.5,2,3.25"))
        assertEquals(listOf(1f, null, 3f), fromStringToListFloat("1,oops,3"))
    }

    @Test
    fun `fromStringToListFloat returns an empty list for an empty string`() {
        assertEquals(emptyList(), fromStringToListFloat(""))
    }

    @Test
    fun `isNewsNew is true for news created well within the threshold`() {
        val dateCreated = Clock.System.now().toEpochMilliseconds() - 2 * 86_400_000L // 2 days ago

        assertTrue(isNewsNew(dateCreated, thresholdDays = 10))
    }

    @Test
    fun `isNewsNew is false for news created before the threshold`() {
        val dateCreated = Clock.System.now().toEpochMilliseconds() - 20 * 86_400_000L // 20 days ago

        assertEquals(false, isNewsNew(dateCreated, thresholdDays = 10))
    }
}
