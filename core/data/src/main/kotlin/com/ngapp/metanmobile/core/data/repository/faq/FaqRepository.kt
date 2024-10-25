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

package com.ngapp.metanmobile.core.data.repository.faq

import com.ngapp.metanmobile.core.data.Syncable
import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.model.news.NewsResource
import kotlinx.coroutines.flow.Flow

/**
 * Encapsulation class for query parameters for [NewsResource]
 */
data class FaqResourceQuery(
    /**
     * News pinned to filter for. Null means any faq item will match.
     */
    val filterFaqListPinned: Boolean = false,
)

interface FaqRepository : Syncable {
    fun getFaqList(query: FaqResourceQuery = FaqResourceQuery(filterFaqListPinned = false)): Flow<List<FaqResource>>
}