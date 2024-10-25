/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.core.ui.model.uitext

import com.ngapp.metanmobile.core.common.result.DataError
import com.ngapp.metanmobile.core.common.result.Result
import com.ngapp.metanmobile.core.ui.R

fun DataError.asUiText(): UiText {
    return when (this) {

        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(
            R.string.core_ui_the_request_timed_out
        )

        DataError.Network.NO_INTERNET -> UiText.StringResource(
            R.string.core_ui_no_internet
        )

        DataError.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.core_ui_server_error
        )

        DataError.Network.UNKNOWN -> UiText.StringResource(
            R.string.core_ui_unknown_error
        )
        DataError.Local.DISK_FULL -> UiText.StringResource(
            R.string.core_ui_error_disk_full
        )

    }
}

fun Result.Error<*, DataError>.asErrorUiText(): UiText {
    return error.asUiText()
}