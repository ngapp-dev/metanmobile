/*
 * Copyright 2025 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.core.network.model.news

import kotlinx.serialization.Serializable

@Serializable
data class NetworkNewsResource(
    val id: String,
    val code: String = "",
    val isPinned: Int = 0,
    val previewPicture: String = "",
    val detailPicture: String = "",
    val isActive: Int = 1,
    val isOperate: Int = 1,
    val relatedStation: String = "",
    val title: String = "",
    val dateCreated: String = "",
    val description: String = "",
    val content: String = "",
    val url: String = "",
)
