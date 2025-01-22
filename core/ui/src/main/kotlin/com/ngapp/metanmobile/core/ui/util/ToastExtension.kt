package com.ngapp.metanmobile.core.ui.util

import android.content.Context
import android.os.Build
import android.widget.Toast
import com.ngapp.metanmobile.core.ui.R

fun Context.showClipboardToast() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        Toast.makeText(
            this,
            getString(R.string.core_ui_text_copied_to_clipboard), Toast.LENGTH_SHORT
        ).show()
    }
}