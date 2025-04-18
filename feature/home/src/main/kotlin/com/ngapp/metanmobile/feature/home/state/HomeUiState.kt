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

package com.ngapp.metanmobile.feature.home.state

import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource

sealed interface HomeUiState {
    data class Success(
        val pinnedNewsList: List<UserNewsResource>,
        val lastNewsList: List<UserNewsResource>,
        val cngPrice: PriceResource?,
        val nearestStation: UserStationResource?,
        val pinnedFaqList: List<FaqResource>,
        val career: CareerResource?,
    ) : HomeUiState

    data object Loading : HomeUiState
}