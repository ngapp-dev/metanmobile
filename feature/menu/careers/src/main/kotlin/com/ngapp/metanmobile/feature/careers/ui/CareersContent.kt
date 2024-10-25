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

package com.ngapp.metanmobile.feature.careers.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.ui.career.CareerRow
import com.ngapp.metanmobile.core.ui.career.CareerRowShimmer

@Composable
internal fun CareersContent(
    modifier: Modifier = Modifier,
    staggeredGridState: LazyStaggeredGridState,
    careers: List<CareerResource>,
) {
    LazyVerticalStaggeredGrid(
        state = staggeredGridState,
        modifier = modifier
            .fillMaxSize()
            .animateContentSize()
            .testTag("careersScreen:feed"),
        columns = StaggeredGridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
    ) {
        if (careers.isNotEmpty()) {
            items(items = careers, key = { career -> career.id }) { career ->
                CareerRow(
                    career = career,
                    cardElevation = 4.dp,
                )
            }
        } else {
            items(10) {
                CareerRowShimmer(cardElevation = 4.dp)
            }
        }
    }
}