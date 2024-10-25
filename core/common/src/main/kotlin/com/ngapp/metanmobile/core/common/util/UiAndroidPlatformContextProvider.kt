package com.ngapp.metanmobile.core.common.util

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

object UiAndroidPlatformContextProvider {
    private var appContext: Context? = null

    val context: Context?
        get() = appContext

    fun setContext(context: Context) {
        appContext = context
    }

    fun Context.getActivity(): ComponentActivity? = when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }
}
