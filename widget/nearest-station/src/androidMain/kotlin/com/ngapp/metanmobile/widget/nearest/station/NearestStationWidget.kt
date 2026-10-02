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
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.ngapp.metanmobile.core.data.repository.widget.WidgetDataRepository
import com.ngapp.metanmobile.widget.nearest.station.state.toNearestStationWidgetUiState
import com.ngapp.metanmobile.widget.nearest.station.ui.NearestStationWidgetContent
import com.ngapp.metanmobile.widget.nearest.station.ui.NearestStationWidgetLayout
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NearestStationWidget : GlanceAppWidget(), KoinComponent {

    private val widgetDataRepository: WidgetDataRepository by inject()

    // Both layouts are handed to the launcher up front, so resizing switches between one and
    // two tiles instantly, without a round trip through the app.
    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(NearestStationWidgetLayout.COMPACT_SIZE, NearestStationWidgetLayout.WIDE_SIZE),
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Read once per render: WidgetPublisher re-renders the widget whenever this data changes.
        val uiState = widgetDataRepository.observeWidgetData().first()
            .toNearestStationWidgetUiState()
        provideContent {
            NearestStationWidgetContent(uiState)
        }
    }
}

class NearestStationWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NearestStationWidget()
}
