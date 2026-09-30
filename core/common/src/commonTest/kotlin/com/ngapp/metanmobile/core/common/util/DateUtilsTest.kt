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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DateUtilsTest {

    @Test
    fun `formatRssDate converts the date returned by the api`() {
        assertEquals(1704207845000L, formatRssDate("Tue, 02 Jan 2024 15:04:05 +0000"))
    }

    @Test
    fun `shortFormatUnixDataToString formats a unix-seconds timestamp as dd-MM-yyyy`() {
        assertTrue(shortFormatUnixDataToString(1718452800L).matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")))
    }

    @Test
    fun `fromStringToListFloat parses values and retains invalid entries as null`() {
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
