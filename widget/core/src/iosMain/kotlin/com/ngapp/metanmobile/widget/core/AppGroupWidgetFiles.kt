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

package com.ngapp.metanmobile.widget.core

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.writeToURL

/**
 * The App Group shared by the app and its WidgetKit extension (both targets must have it in
 * Signing & Capabilities). The extension can't run the app's Kotlin code or open its database,
 * so widgets get their data as files in this group's container.
 */
const val WIDGET_APP_GROUP = "group.com.ngapp.metanmobile"

/**
 * Asks WidgetKit to redraw the widgets. WidgetCenter is a Swift-only API, so the app's Swift
 * side implements this and hands it to Kotlin at startup (see initSharedKoin).
 */
fun interface WidgetReloader {
    fun reloadAllWidgets()
}

/** Writes widget data files into the [WIDGET_APP_GROUP] container. */
class AppGroupWidgetFiles(private val groupId: String = WIDGET_APP_GROUP) {

    /** False when the container is unavailable, i.e. the App Group capability is missing. */
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    fun write(fileName: String, content: String): Boolean {
        val container = NSFileManager.defaultManager
            .containerURLForSecurityApplicationGroupIdentifier(groupId)
        if (container == null) {
            println("AppGroupWidgetFiles: no container for $groupId - is the App Group capability set?")
            return false
        }
        val file = container.URLByAppendingPathComponent(fileName) ?: return false
        @Suppress("CAST_NEVER_SUCCEEDS")
        val text = NSString.create(string = content)
        return text.writeToURL(file, atomically = true, encoding = NSUTF8StringEncoding, error = null)
    }
}
