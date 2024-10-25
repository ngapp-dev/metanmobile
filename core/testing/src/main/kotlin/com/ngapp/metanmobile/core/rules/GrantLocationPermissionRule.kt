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

package com.ngapp.metanmobile.core.rules

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.test.rule.GrantPermissionRule.grant
import org.junit.rules.TestRule

/**
 * [TestRule] that grants [ACCESS_COARSE_LOCATION] and [ACCESS_FINE_LOCATION] permissions.
 * It handles the case where the test is run on an SDK version that supports runtime permissions.
 */
class GrantLocationPermissionRule : TestRule by if (SDK_INT >= Build.VERSION_CODES.M) {
    grant(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
} else {
    TestRule { base, _ -> base }
}