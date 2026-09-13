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

package com.ngapp.metanmobile.core.designsystem.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Delays visibility of given [content] for [delayMillis].
 */
@Composable
fun Delayed(
    modifier: Modifier = Modifier,
    delayMillis: Long = 200,
    content: @Composable () -> Unit
) {
    TimedVisibility(
        delayMillis = delayMillis,
        visibility = false,
        modifier = modifier,
        content = content
    )
}

/**
 * Changes visibility of given [content] after [delayMillis] to opposite of initial [visibility].
 */
@Composable
fun TimedVisibility(
    modifier: Modifier = Modifier,
    delayMillis: Long = 4000,
    visibility: Boolean = true,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(visibility) }
    val coroutine = rememberCoroutineScope()

    DisposableEffect(Unit) {
        val job = coroutine.launch {
            delay(delayMillis)
            visible = !visible
        }

        onDispose {
            job.cancel()
        }
    }
    AnimatedVisibility(visible = visible, modifier = modifier, enter = fadeIn(), exit = fadeOut()) {
        content()
    }
}