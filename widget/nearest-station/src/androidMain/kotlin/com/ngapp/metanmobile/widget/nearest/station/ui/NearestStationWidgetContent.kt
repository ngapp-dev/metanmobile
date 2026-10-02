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
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.widget.core.theme.WidgetDimens
import com.ngapp.metanmobile.widget.core.theme.WidgetPalette
import com.ngapp.metanmobile.widget.core.theme.WidgetStatus
import com.ngapp.metanmobile.widget.core.theme.WidgetTextStyles
import com.ngapp.metanmobile.widget.core.theme.palette
import com.ngapp.metanmobile.widget.core.ui.WidgetContainer
import com.ngapp.metanmobile.widget.core.ui.WidgetStatusDot
import com.ngapp.metanmobile.widget.core.ui.WidgetTile
import com.ngapp.metanmobile.widget.nearest.station.settings.toNearestStationWidgetSettings
import com.ngapp.metanmobile.widget.nearest.station.state.NearestStationTiles
import com.ngapp.metanmobile.widget.nearest.station.state.NearestStationWidgetUiState
import com.ngapp.metanmobile.widget.nearest.station.state.formatDistanceKm
import dev.icerock.moko.resources.StringResource

/** The two layouts the widget switches between as it's resized (see SizeMode.Responsive). */
internal object NearestStationWidgetLayout {
    /** About 2 cells: one tile. */
    val COMPACT_SIZE = DpSize(110.dp, 40.dp)

    /** About 4 cells: both tiles fit with their texts. */
    val WIDE_SIZE = DpSize(250.dp, 40.dp)
}

// Handled by MainActivity like any other App Link (see MetanMobileAppState.navigateToDeepLink).
private const val STATION_DEEP_LINK = "https://metan.by/ecogas-map"

@Composable
internal fun NearestStationWidgetContent(uiState: NearestStationWidgetUiState) {
    val context = LocalContext.current
    val settings = currentState<Preferences>().toNearestStationWidgetSettings()
    val palette = settings.appearance.palette()
    // The user's choice, narrowed to one tile when the widget is too narrow for two.
    val isWide = LocalSize.current.width >= NearestStationWidgetLayout.WIDE_SIZE.width
    val tiles = settings.tiles.tilesFor(isWide)
    val showsPrice = tiles.showsPrice
    val showsStation = tiles.showsStation
    WidgetContainer(
        palette = palette,
        modifier = GlanceModifier.clickable(openAppAction(context)),
        // One tile in a wide widget takes half of it instead of stretching across.
        halfWidth = isWide && tiles != NearestStationTiles.BOTH,
    ) {
        when (uiState) {
            NearestStationWidgetUiState.NoData -> WidgetTile(palette, GlanceModifier.fillMaxWidth()) {
                Caption(context.string(SharedRes.strings.widget_text_no_data), palette, maxLines = 3)
            }

            is NearestStationWidgetUiState.Success -> Row(modifier = GlanceModifier.fillMaxWidth()) {
                if (showsPrice) {
                    PriceTile(
                        cngPrice = uiState.cngPrice,
                        palette = palette,
                        modifier = GlanceModifier.defaultWeight(),
                    )
                }
                if (showsPrice && showsStation) {
                    Spacer(GlanceModifier.width(WidgetDimens.tileSpacing))
                }
                if (showsStation) {
                    StationTile(
                        nearestStation = uiState.nearestStation,
                        palette = palette,
                        modifier = GlanceModifier.defaultWeight(),
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceTile(cngPrice: PriceResource?, palette: WidgetPalette, modifier: GlanceModifier) {
    val context = LocalContext.current
    WidgetTile(palette, modifier) {
        Value(context.string(SharedRes.strings.core_ui_text_value_byn, cngPrice?.content ?: "—"), palette)
        Caption(context.string(SharedRes.strings.core_ui_text_cng_price), palette)
        Caption(context.string(SharedRes.strings.core_ui_text_for_one_meter), palette)
    }
}

@Composable
private fun StationTile(nearestStation: UserStationResource?, palette: WidgetPalette, modifier: GlanceModifier) {
    val context = LocalContext.current
    if (nearestStation == null) {
        // Location unknown: the app asks for the permission / finds the location on open.
        WidgetTile(palette, modifier) {
            Caption(
                context.string(SharedRes.strings.widget_nearest_station_text_no_location),
                palette,
                maxLines = 4,
            )
        }
        return
    }
    WidgetTile(palette, modifier.clickable(openStationAction(context, nearestStation.code))) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Value(
                context.string(
                    SharedRes.strings.core_ui_text_value_km,
                    formatDistanceKm(nearestStation.distanceBetween ?: 0.0),
                ),
                palette,
            )
            WidgetStatusDot(nearestStation.widgetStatus, palette)
        }
        Caption(context.string(SharedRes.strings.core_ui_text_nearest_station), palette)
        Caption(nearestStation.address, palette)
    }
}

@Composable
private fun Value(text: String, palette: WidgetPalette) {
    Text(text = text, style = WidgetTextStyles.value(palette.onTile.provider), maxLines = 1)
}

@Composable
private fun Caption(text: String, palette: WidgetPalette, maxLines: Int = 1) {
    Text(
        text = text,
        style = WidgetTextStyles.caption(palette.onTile.provider),
        maxLines = maxLines,
        modifier = GlanceModifier.padding(top = 2.dp),
    )
}

internal val UserStationResource.widgetStatus: WidgetStatus
    get() = when (isOperate) {
        1 -> WidgetStatus.OK
        0 -> WidgetStatus.PROBLEM
        else -> WidgetStatus.UNKNOWN
    }

private fun openAppAction(context: Context): Action {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        ?: Intent().setPackage(context.packageName)
    return actionStartActivity(intent)
}

private fun openStationAction(context: Context, stationCode: String): Action =
    actionStartActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse("$STATION_DEEP_LINK/$stationCode/"))
            .setPackage(context.packageName),
    )

private fun Context.string(resource: StringResource, vararg args: Any): String =
    getString(resource.resourceId, *args)
