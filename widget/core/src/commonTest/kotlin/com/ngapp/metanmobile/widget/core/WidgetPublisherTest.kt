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
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.widget.WidgetData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WidgetPublisherTest {

    private val repository = FakeWidgetDataRepository()

    @Test
    fun startingPushesTheCurrentDataToEveryWidget() = runTest {
        val first = RecordingUpdater()
        val second = RecordingUpdater()

        startPublisher(first, second)

        assertEquals(listOf(emptyData), first.received)
        assertEquals(listOf(emptyData), second.received)
    }

    @Test
    fun widgetsAreUpdatedWhenLocalDataChanges() = runTest {
        val updater = RecordingUpdater()
        startPublisher(updater)

        val withPrice = emptyData.copy(cngPrice = PriceResource.init().copy(content = "1.16"))
        repository.data.value = withPrice
        runCurrent()

        assertEquals(listOf(emptyData, withPrice), updater.received)
    }

    @Test
    fun oneFailingWidgetDoesNotKeepTheOthersStale() = runTest {
        val healthy = RecordingUpdater()

        startPublisher(WidgetUpdater { error("broken widget") }, healthy)

        assertEquals(listOf(emptyData), healthy.received)
    }

    @Test
    fun withoutWidgetsNothingIsObserved() = runTest {
        startPublisher()

        assertEquals(0, repository.subscriptions)
    }

    @Test
    fun refreshPushesTheCurrentDataAgain() = runTest {
        val updater = RecordingUpdater()
        val publisher = WidgetPublisher(repository, listOf(updater))

        publisher.refresh()

        assertEquals(listOf(emptyData), updater.received)
    }

    // runCurrent, not advanceUntilIdle: the latter doesn't run backgroundScope work.
    private fun TestScope.startPublisher(vararg updaters: WidgetUpdater) {
        val job = WidgetPublisher(repository, updaters.toList()).start(backgroundScope)
        runCurrent()
        assertTrue(job.isActive || updaters.isEmpty())
    }

    private class RecordingUpdater : WidgetUpdater {
        val received = mutableListOf<WidgetData>()

        override suspend fun update(data: WidgetData) {
            received += data
        }
    }

    private class FakeWidgetDataRepository : WidgetDataRepository {
        val data = MutableStateFlow(emptyData)
        var subscriptions = 0

        override fun observeWidgetData(): Flow<WidgetData> {
            subscriptions++
            return data
        }
    }

    private companion object {
        val emptyData = WidgetData(cngPrice = null, location = null, stations = emptyList())
    }
}
