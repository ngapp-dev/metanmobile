package com.ngapp.metanmobile.core.ui.util

import android.content.Intent
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.ngapp.metanmobile.core.ui.UiAndroidPlatformContextProvider

actual fun openOssLicenses() {
    val context = UiAndroidPlatformContextProvider.context ?: return
    context.startActivity(
        Intent(context, OssLicensesMenuActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}
