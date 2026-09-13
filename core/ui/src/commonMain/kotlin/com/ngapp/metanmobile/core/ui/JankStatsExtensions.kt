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

package com.ngapp.metanmobile.core.ui

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope

/**
 * Jank-tracking state handle. Backed by AndroidX's `PerformanceMetricsState` on Android
 * (see the Android `Now in Android` sample this pattern comes from); a no-op elsewhere, since
 * there's no equivalent frame-metrics API to plug into.
 */
expect class JankMetricsHolder {
    fun putState(key: String, value: String)
    fun removeState(key: String)
}

@Composable
expect fun rememberMetricsStateHolder(): JankMetricsHolder

/**
 * Convenience function to work with [JankMetricsHolder]. The side effect is re-launched if any
 * of the [keys] value is not equal to the previous composition.
 * @see TrackDisposableJank if you need to work with DisposableEffect to cleanup added state.
 */
@Composable
fun TrackJank(
    vararg keys: Any,
    reportMetric: suspend CoroutineScope.(state: JankMetricsHolder) -> Unit,
) {
    val metrics = rememberMetricsStateHolder()
    LaunchedEffect(metrics, *keys) {
        reportMetric(metrics)
    }
}

/**
 * Convenience function to work with [JankMetricsHolder] that needs to be cleaned up.
 * The side effect is re-launched if any of the [keys] value is not equal to the previous composition.
 */
@Composable
fun TrackDisposableJank(
    vararg keys: Any,
    reportMetric: DisposableEffectScope.(state: JankMetricsHolder) -> DisposableEffectResult,
) {
    val metrics = rememberMetricsStateHolder()
    DisposableEffect(metrics, *keys) {
        reportMetric(this, metrics)
    }
}

/**
 * Track jank while scrolling anything that's scrollable.
 */
@Composable
fun TrackScrollJank(scrollableState: ScrollableState, stateName: String) {
    TrackJank(scrollableState) { metricsHolder ->
        snapshotFlow { scrollableState.isScrollInProgress }.collect { isScrollInProgress ->
            if (isScrollInProgress) {
                metricsHolder.putState(stateName, "Scrolling=true")
            } else {
                metricsHolder.removeState(stateName)
            }
        }
    }
}
