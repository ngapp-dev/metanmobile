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

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

/**
 * Gives `androidMain` platform code (share, chrome tabs, permissions, ads) access to an Android
 * [Context] without threading it through every composable. Set once from `MainActivity.onCreate`
 * with the Activity itself (not just the Application context) so [getActivity] below can resolve
 * it — [ConsentHelper][com.ngapp.metanmobile.core.ui.ads.ConsentHelper]'s UMP/MobileAds calls and
 * in-app review need a real Activity, not just any Context.
 */
object UiAndroidPlatformContextProvider {
    private var appContext: Context? = null

    val context: Context?
        get() = appContext

    fun setContext(context: Context) {
        appContext = context
    }

    fun Context.getActivity(): ComponentActivity? = when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }
}
