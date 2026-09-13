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

import android.os.Build
import android.widget.Toast
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.ui.UiAndroidPlatformContextProvider

actual fun showToast(message: String) {
    val context = UiAndroidPlatformContextProvider.context ?: return
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

actual fun showClipboardToast() {
    // Android 12+ (S) already shows its own system "copied" confirmation.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        val context = UiAndroidPlatformContextProvider.context ?: return
        val message = context.getString(SharedRes.strings.core_ui_text_copied_to_clipboard.resourceId)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
