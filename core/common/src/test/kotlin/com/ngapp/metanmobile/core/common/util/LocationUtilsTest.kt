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

import org.junit.Test
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocationUtilsTest {

    @Test
    fun `distanceInKm is zero for the same point`() {
        val distance = distanceInKm(53.9006, 27.5590, 53.9006, 27.5590)

        assertEquals(0.0, distance, 0.001)
    }

    @Test
    fun `distanceInKm matches the known great-circle distance between two cities`() {
        // Minsk to Grodno, roughly 260 km apart in a straight line.
        val minskLat = 53.9006
        val minskLon = 27.5590
        val grodnoLat = 53.6884
        val grodnoLon = 23.8258

        val distance = distanceInKm(minskLat, minskLon, grodnoLat, grodnoLon)

        assertTrue(
            abs(distance - 260.0) < 15.0,
            "Expected roughly 260 km between Minsk and Grodno, got $distance km",
        )
    }

    @Test
    fun `distanceInKm is symmetric`() {
        val aToB = distanceInKm(53.9006, 27.5590, 53.6884, 23.8258)
        val bToA = distanceInKm(53.6884, 23.8258, 53.9006, 27.5590)

        assertEquals(aToB, bToA, 0.001)
    }
}
