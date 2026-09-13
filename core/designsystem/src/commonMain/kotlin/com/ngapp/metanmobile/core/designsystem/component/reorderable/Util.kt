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

package com.ngapp.metanmobile.core.designsystem.component.reorderable

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.sync.Mutex

internal fun Offset.getAxis(orientation: Orientation) = when (orientation) {
    Orientation.Vertical -> y
    Orientation.Horizontal -> x
}

internal fun Size.getAxis(orientation: Orientation) = when (orientation) {
    Orientation.Vertical -> height
    Orientation.Horizontal -> width
}

internal fun IntOffset.getAxis(orientation: Orientation) = when (orientation) {
    Orientation.Vertical -> y
    Orientation.Horizontal -> x
}

internal fun IntSize.getAxis(orientation: Orientation) = when (orientation) {
    Orientation.Vertical -> height
    Orientation.Horizontal -> width
}

internal fun Offset.Companion.fromAxis(orientation: Orientation, value: Float) =
    when (orientation) {
        Orientation.Vertical -> Offset(0f, value)
        Orientation.Horizontal -> Offset(value, 0f)
    }

internal fun Offset.onlyAxis(orientation: Orientation) =
    when (orientation) {
        Orientation.Vertical -> Offset(0f, y)
        Orientation.Horizontal -> Offset(x, 0f)
    }

internal fun Offset.reverseAxis(orientation: Orientation) =
    when (orientation) {
        Orientation.Vertical -> Offset(x, -y)
        Orientation.Horizontal -> Offset(-x, y)
    }

internal fun IntSize.Companion.fromAxis(orientation: Orientation, value: Float) =
    when (orientation) {
        Orientation.Vertical -> Size(0f, value)
        Orientation.Horizontal -> Size(value, 0f)
    }

internal fun IntOffset.Companion.fromAxis(orientation: Orientation, value: Int) =
    when (orientation) {
        Orientation.Vertical -> IntOffset(0, value)
        Orientation.Horizontal -> IntOffset(value, 0)
    }

internal fun IntSize.Companion.fromAxis(orientation: Orientation, value: Int) =
    when (orientation) {
        Orientation.Vertical -> IntSize(0, value)
        Orientation.Horizontal -> IntSize(value, 0)
    }

internal operator fun Offset.plus(size: Size) = Offset(x + size.width, y + size.height)
internal operator fun Offset.minus(size: Size) = Offset(x - size.width, y - size.height)

internal operator fun IntOffset.plus(size: IntSize) = IntOffset(x + size.width, y + size.height)
internal operator fun IntOffset.minus(size: IntSize) = IntOffset(x - size.width, y - size.height)


internal fun Mutex.withTryLock(block: () -> Unit): Boolean {
    return if (tryLock()) {
        block()
        unlock()
        true
    } else {
        false
    }
}
