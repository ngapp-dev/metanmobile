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

import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual class ShareManager actual constructor() {

    actual fun createShareStationIntent(station: UserStationResource?) {
        val message = "${station?.title}\n${station?.address}\n${station?.url}"
        present(message)
    }

    actual fun createShareNewsIntent(news: NewsResource?) {
        val message = "${news?.title}\n${news?.description}\n${news?.url}"
        present(message)
    }

    private fun present(message: String) {
        val activityViewController = UIActivityViewController(
            activityItems = listOf(message),
            applicationActivities = null,
        )
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(activityViewController, animated = true, completion = null)
    }
}
