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

import android.content.Intent
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.model.station.UserStationResource

actual class ShareManager actual constructor() {

    private val context = requireNotNull(UiAndroidPlatformContextProvider.context)

    private fun shareVia(): String = context.getString(SharedRes.strings.core_ui_text_share_via.resourceId) +
        context.getString(SharedRes.strings.core_ui_app_name.resourceId)

    actual fun createShareStationIntent(station: UserStationResource?) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, station?.title + shareVia())
            putExtra(
                Intent.EXTRA_TEXT,
                station?.title + "\n${station?.address}" + "\n${station?.url}"
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }

    actual fun createShareNewsIntent(news: NewsResource?) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, news?.title + shareVia())
            putExtra(
                Intent.EXTRA_TEXT,
                news?.title + "\n${news?.description}" + "\n${news?.url}"
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }
}
