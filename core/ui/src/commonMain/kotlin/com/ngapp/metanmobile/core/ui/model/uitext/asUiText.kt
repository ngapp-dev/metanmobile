package com.ngapp.metanmobile.core.ui.model.uitext

import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.common.result.DataError
import com.ngapp.metanmobile.core.common.result.Result

fun DataError.asUiText(): UiText = UiText.Resource(
    when (this) {
        DataError.Network.REQUEST_TIMEOUT -> SharedRes.strings.core_ui_request_timeout
        DataError.Network.NO_INTERNET -> SharedRes.strings.core_ui_no_internet
        DataError.Network.SERVER_ERROR -> SharedRes.strings.core_ui_server_error
        DataError.Network.UNKNOWN -> SharedRes.strings.core_ui_unknown_error
        DataError.Local.DISK_FULL -> SharedRes.strings.core_ui_disk_full
    }
)

fun Result.Error<*, DataError>.asErrorUiText(): UiText = error.asUiText()
