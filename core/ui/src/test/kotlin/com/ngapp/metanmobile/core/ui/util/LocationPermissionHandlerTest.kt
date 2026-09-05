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

package com.ngapp.metanmobile.core.ui.util

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * [Accompanist's `shouldShowRationale`](https://google.github.io/accompanist/permissions/) is
 * `false` both before the very first request and once the permission is permanently denied -
 * these tests pin down how [shouldShowSystemDialog] uses [hasRequestedBefore] to tell those two
 * apart, which is the fix for: after "don't ask again", the "Request permission" button on
 * Home's right widget / the stations list did nothing at all when clicked again.
 */
class LocationPermissionHandlerTest {

    @Test
    fun `never requested before - shows the system dialog regardless of canAskAgain`() {
        assertTrue(shouldShowSystemDialog(hasRequestedBefore = false, canAskAgain = false))
        assertTrue(shouldShowSystemDialog(hasRequestedBefore = false, canAskAgain = true))
    }

    @Test
    fun `requested before and can still ask - shows the system dialog`() {
        assertTrue(shouldShowSystemDialog(hasRequestedBefore = true, canAskAgain = true))
    }

    @Test
    fun `requested before and permanently denied - does not show the system dialog`() {
        // This is the bug: launchMultiplePermissionRequest() here shows no UI and the caller was
        // left stuck waiting for a status change that never comes - the caller must fall back to
        // opening Settings instead.
        assertFalse(shouldShowSystemDialog(hasRequestedBefore = true, canAskAgain = false))
    }
}
