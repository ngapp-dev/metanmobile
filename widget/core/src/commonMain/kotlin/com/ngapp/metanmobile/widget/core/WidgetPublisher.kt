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

import com.ngapp.metanmobile.core.data.repository.widget.WidgetDataRepository
import com.ngapp.metanmobile.core.model.widget.WidgetData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Keeps every widget in step with the app's local data while the process is alive: a finished
 * sync, a new user location or a changed favorite re-renders the widgets. No network involved.
 */
class WidgetPublisher(
    private val widgetDataRepository: WidgetDataRepository,
    private val updaters: List<WidgetUpdater>,
) {
    fun start(scope: CoroutineScope): Job = scope.launch {
        if (updaters.isEmpty()) return@launch
        widgetDataRepository.observeWidgetData().collectLatest(::publish)
    }

    /** Re-renders the widgets right away, e.g. after the user changed how one looks. */
    suspend fun refresh() {
        publish(widgetDataRepository.observeWidgetData().first())
    }

    private suspend fun publish(data: WidgetData) {
        updaters.forEach { updater ->
            try {
                updater.update(data)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // One broken widget shouldn't keep the others stale.
                println("WidgetPublisher: update failed: ${e::class.simpleName}: ${e.message}")
            }
        }
    }
}
