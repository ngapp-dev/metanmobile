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

package com.ngapp.metanmobile.core.model.career

import kotlinx.datetime.Clock

data class CareerResource(
    val id: String,
    val code: String,
    val previewPicture: String,
    val detailPicture: String,
    val isActive: Int,
    val title: String,
    val dateCreated: Long,
    val exp: String,
    val place: String,
    val description: String,
    val requirements: String,
    val responsibilities: String,
    val number: Int,
) {
    companion object {
        fun init() = CareerResource(
            id = "1",
            code = "",
            previewPicture = "",
            detailPicture = "",
            isActive = 1,
            title = "Repairman of a comprehensive repair team",
            dateCreated = Clock.System.now().toEpochMilliseconds(),
            exp = "2 years",
            place = "Orsha",
            description = "• Official employment from the first day of work in accordance with the Labor Code of the Republic of Belarus;",
            requirements = "Secondary technical or secondary specialized education in the field;",
            responsibilities = "Repair equipment",
            number = 1
        )
    }
}