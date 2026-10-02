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

package com.ngapp.metanmobile.widget.nearest.station.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.glance.appwidget.testing.unit.GlanceAppWidgetUnitTest
import androidx.glance.appwidget.testing.unit.hasStartActivityClickAction
import androidx.glance.appwidget.testing.unit.runGlanceAppWidgetUnitTest
import androidx.glance.testing.unit.hasText
import androidx.test.core.app.ApplicationProvider
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.widget.nearest.station.settings.NearestStationWidgetSettings
import com.ngapp.metanmobile.widget.nearest.station.settings.writeNearestStationWidgetSettings
import com.ngapp.metanmobile.widget.nearest.station.state.NearestStationTiles
import com.ngapp.metanmobile.widget.nearest.station.state.NearestStationWidgetUiState
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The widget's Glance layout, rendered on the JVM: which tiles a size and the settings produce,
 * the fallback messages, and where a tap on the station goes.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NearestStationWidgetContentTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val station = UserStationResource.init().copy(
        code = "agnks_grodno2",
        address = "Grodno, Indurskoe 15",
        distanceBetween = 2.36,
        isOperate = 1,
    )
    private val success = NearestStationWidgetUiState.Success(
        cngPrice = PriceResource.init().copy(content = "1.16"),
        nearestStation = station,
    )

    @Test
    fun aWideWidgetShowsBothTiles() = renderWidget(success, wide = true) {
        onNode(hasText(PRICE)).assertExists()
        onNode(hasText(DISTANCE)).assertExists()
        onNode(hasText("Grodno, Indurskoe 15")).assertExists()
    }

    @Test
    fun aNarrowWidgetKeepsOnlyThePrice() = renderWidget(success, wide = false) {
        onNode(hasText(PRICE)).assertExists()
        onNode(hasText(DISTANCE)).assertDoesNotExist()
    }

    @Test
    fun theChosenSingleTileIsShownEvenWhenWide() =
        renderWidget(success, wide = true, tiles = NearestStationTiles.DISTANCE) {
            onNode(hasText(DISTANCE)).assertExists()
            onNode(hasText(PRICE)).assertDoesNotExist()
        }

    @Test
    fun aNarrowWidgetKeepsTheChosenDistance() =
        renderWidget(success, wide = false, tiles = NearestStationTiles.DISTANCE) {
            onNode(hasText(DISTANCE)).assertExists()
            onNode(hasText(PRICE)).assertDoesNotExist()
        }

    @Test
    fun anUnknownLocationAsksToOpenTheApp() =
        renderWidget(success.copy(nearestStation = null), wide = true) {
            onNode(hasText(PRICE)).assertExists()
            onNode(hasText("Open the app to find the nearest station")).assertExists()
        }

    @Test
    fun nothingSyncedYetAsksToOpenTheApp() = renderWidget(NearestStationWidgetUiState.NoData, wide = true) {
        onNode(hasText("Open the app to load the data")).assertExists()
        onNode(hasText(PRICE)).assertDoesNotExist()
    }

    @Test
    fun tappingTheStationOpensItInTheApp() = renderWidget(success, wide = true) {
        val stationLink = Intent(Intent.ACTION_VIEW, Uri.parse("https://metan.by/ecogas-map/agnks_grodno2/"))
            .setPackage(context.packageName)
        onNode(hasStartActivityClickAction(stationLink)).assertExists()
    }

    private fun renderWidget(
        uiState: NearestStationWidgetUiState,
        wide: Boolean,
        tiles: NearestStationTiles = NearestStationTiles.BOTH,
        assertions: GlanceAppWidgetUnitTest.() -> Unit,
    ) = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(
            if (wide) NearestStationWidgetLayout.WIDE_SIZE else NearestStationWidgetLayout.COMPACT_SIZE,
        )
        setState<Preferences>(
            mutablePreferencesOf().apply {
                writeNearestStationWidgetSettings(NearestStationWidgetSettings(tiles = tiles))
            },
        )
        provideComposable { NearestStationWidgetContent(uiState) }
        assertions()
    }

    private companion object {
        // English resources, the default locale under Robolectric.
        const val PRICE = "1.16 BYN"
        const val DISTANCE = "2,4 KM"
    }
}
