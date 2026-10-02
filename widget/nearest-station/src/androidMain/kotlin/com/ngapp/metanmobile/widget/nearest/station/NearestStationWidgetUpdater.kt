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

package com.ngapp.metanmobile.widget.nearest.station

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.ngapp.metanmobile.core.model.widget.WidgetData
import com.ngapp.metanmobile.widget.core.WidgetUpdater

internal class NearestStationWidgetUpdater(
    private val context: Context,
) : WidgetUpdater {
    // No-op when the user hasn't placed this widget anywhere.
    override suspend fun update(data: WidgetData) {
        NearestStationWidget().updateAll(context)
    }
}
