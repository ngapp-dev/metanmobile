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

import androidx.compose.runtime.Composable
import com.ngapp.metanmobile.core.ui.LocalTimeZone
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime

/** Formats [dateCreated] (epoch millis) as "dd.MM.yyyy" in the currently provided time zone. */
@Composable
fun dateFormatted(dateCreated: Long): String {
    val localDateTime = Instant.fromEpochMilliseconds(dateCreated).toLocalDateTime(LocalTimeZone.current)
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val month = localDateTime.monthNumber.toString().padStart(2, '0')
    return "$day.$month.${localDateTime.year}"
}
