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

package com.ngapp.metanmobile.core.designsystem.component.htmltext

import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration

internal fun UnderlineSpan.spanStyle(): SpanStyle =
    SpanStyle(textDecoration = TextDecoration.Underline)

internal fun ForegroundColorSpan.spanStyle(): SpanStyle =
    SpanStyle(color = Color(foregroundColor))

internal fun StrikethroughSpan.spanStyle(): SpanStyle =
    SpanStyle(textDecoration = TextDecoration.LineThrough)