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

package com.ngapp.metanmobile.core.analytics

import kotlin.test.Test
import kotlin.test.assertEquals

class BridgedAnalyticsHelperTest {

    private val logged = mutableListOf<Pair<String, Map<String, String>>>()
    private val helper = BridgedAnalyticsHelper { name, parameters -> logged += name to parameters }

    @Test
    fun `forwards the event type and its params`() {
        helper.logEvent(
            AnalyticsEvent(
                type = AnalyticsEvent.Types.SCREEN_VIEW,
                extras = listOf(AnalyticsEvent.Param(AnalyticsEvent.ParamKeys.SCREEN_NAME, "HomeScreen")),
            ),
        )

        assertEquals(listOf("screen_view" to mapOf("screen_name" to "HomeScreen")), logged)
    }

    @Test
    fun `forwards an event without params`() {
        helper.logEvent(AnalyticsEvent(type = "network_sync_started"))

        assertEquals(listOf("network_sync_started" to emptyMap<String, String>()), logged)
    }

    @Test
    fun `truncates keys to 40 and values to 100 characters`() {
        helper.logEvent(AnalyticsEvent(type = "e", extras = listOf(AnalyticsEvent.Param("k".repeat(50), "v".repeat(150)))))

        assertEquals(mapOf("k".repeat(40) to "v".repeat(100)), logged.single().second)
    }
}
