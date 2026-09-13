package com.ngapp.metanmobile.core.ui.util

import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler as AndroidBackHandler

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) = AndroidBackHandler(enabled, onBack)
