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

package com.ngapp.metanmobile.widget.core.settings

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.widget.core.WidgetPublisher
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Base for a widget's configuration screen — opened from the launcher ("Settings" on a
 * long-pressed widget, Android 12+) or when the widget is added on older Android. Handles the
 * system contract and the save → re-render round trip; a widget only says what its settings
 * [S] are, where they're stored and how they're edited (see WidgetSettingsComponents).
 */
abstract class WidgetSettingsActivity<S> : ComponentActivity() {

    private val widgetPublisher: WidgetPublisher by inject()

    protected abstract suspend fun loadSettings(context: Context, glanceId: GlanceId): S

    protected abstract suspend fun saveSettings(context: Context, glanceId: GlanceId, settings: S)

    @Composable
    protected abstract fun SettingsScreen(initialSettings: S, onSave: (S) -> Unit)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        // Leaving without saving while a widget is being added cancels adding it.
        setResult(RESULT_CANCELED, result)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        enableEdgeToEdge()

        lifecycleScope.launch {
            val context = this@WidgetSettingsActivity
            val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            val initialSettings = loadSettings(context, glanceId)
            setContent {
                MMTheme {
                    SettingsScreen(
                        initialSettings = initialSettings,
                        onSave = { settings ->
                            lifecycleScope.launch {
                                saveSettings(context, glanceId, settings)
                                widgetPublisher.refresh()
                                setResult(RESULT_OK, result)
                                finish()
                            }
                        },
                    )
                }
            }
        }
    }
}
