/*
 * Copyright 2025 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.core.ui.stations

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.R

fun Context.createLocationIntent(station: UserStationResource) {
    val uriYandex = if (station.yandexTag.isNotEmpty()) {
        "https://yandex.ru/maps/org/${station.yandexTag}"
    } else {
        "yandexmaps://maps.yandex.ru/?pt=0,0&z=12&text=${station.latitude},${station.longitude}&l=map"
    }
    val intentYandex = Intent(Intent.ACTION_VIEW, Uri.parse(uriYandex))
    intentYandex.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intentYandex.setPackage("ru.yandex.yandexmaps")


    val uriGoogle = if (station.googleMapsTag.isNotEmpty()) {
        "https://maps.google.com/maps?cid=${station.googleMapsTag}"
    } else {
        "geo:0,0?q=${station.latitude},${station.longitude}"
    }
    val intentGoogle = Intent(Intent.ACTION_VIEW, Uri.parse(uriGoogle))
    intentGoogle.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intentGoogle.setPackage("com.google.android.apps.maps")

    val title = getString(R.string.core_ui_text_select_application)
    val chooserIntent = Intent.createChooser(intentGoogle, title)
    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val arr = arrayOfNulls<Intent>(1)
    arr[0] = intentYandex
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arr)
    val activity = (this as ContextWrapper).baseContext
    activity.startActivity(chooserIntent)
}