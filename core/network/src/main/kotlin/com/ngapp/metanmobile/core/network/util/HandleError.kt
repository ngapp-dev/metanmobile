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

package com.ngapp.metanmobile.core.network.util

import com.ngapp.metanmobile.core.common.result.DataError
import com.ngapp.metanmobile.core.common.result.Result
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

fun <T> handleError(exception: Exception): Result<T, DataError.Network> {
    return when (exception) {
        is HttpException -> {
            val error = when (exception.code()) {
                404 -> DataError.Network.UNKNOWN
                408 -> DataError.Network.REQUEST_TIMEOUT
                else -> DataError.Network.UNKNOWN
            }
            Result.Error(error)
        }

        is ConnectException -> Result.Error(DataError.Network.NO_INTERNET)
        is SocketTimeoutException -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}

