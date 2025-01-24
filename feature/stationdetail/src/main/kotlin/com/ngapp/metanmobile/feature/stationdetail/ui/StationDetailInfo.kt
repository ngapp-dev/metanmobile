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

package com.ngapp.metanmobile.feature.stationdetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMShapes
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.ItemDetailImageView
import com.ngapp.metanmobile.core.ui.news.NewsRow
import com.ngapp.metanmobile.core.ui.stations.StationBusyHours
import com.ngapp.metanmobile.core.ui.stations.StationInfoListRow
import com.ngapp.metanmobile.core.ui.stations.StationWorkTimeRow
import com.ngapp.metanmobile.core.ui.R as CoreUiR
import com.ngapp.metanmobile.core.ui.stations.StationInfoRow
import com.ngapp.metanmobile.feature.stationdetail.R

@Composable
internal fun StationDetailOverview(
    stationDetail: UserStationResource,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        StationInfoRow(
            isExpandable = false,
            rowIcon = MMIcons.LocationOnOutlined,
            text = stationDetail.address,
        )
        StationInfoRow(
            isExpandable = false,
            rowIcon = MMIcons.ExploreOutlined,
            text = "${stationDetail.latitude}, ${stationDetail.longitude}",
        )
        StationWorkTimeRow(
            rowIcon = MMIcons.ClockOutlined,
            workingTime = stationDetail.workingTime,
        )
        StationInfoListRow(
            rowIcon = MMIcons.CallFilled,
            text = stationDetail.phones,
        )
        StationBusyHours(stationDetail = stationDetail)
    }
}

@Composable
internal fun StationDetailUpdates(
    relatedNewsList: List<UserNewsResource>,
    onNewsDetailClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (relatedNewsList.isEmpty()) {
        Text(
            text = stringResource(CoreUiR.string.core_ui_text_no_news),
            textAlign = TextAlign.Center,
            color = MMColors.inverseSurface,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    } else {
        Column {
            relatedNewsList.forEach { relatedNews ->
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(MMColors.cardBackgroundColor)
                ) {
                    NewsRow(
                        news = relatedNews,
                        onDetailClick = { onNewsDetailClick(relatedNews.id) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun StationDetailPayments(
    stationDetail: UserStationResource,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        StationInfoListRow(
            isClickable = false,
            rowIcon = MMIcons.PaymentsOutlined,
            text = stationDetail.payment,
        )
        StationInfoRow(
            isExpandable = false,
            rowIcon = MMIcons.ContractOutlined,
            text = stringResource(R.string.feature_stationdetail_text_individual_contract),
            url = "https://metan.by/services/contracts/9895/",
        )
        StationInfoRow(
            isExpandable = false,
            rowIcon = MMIcons.ContractOutlined,
            text = stringResource(R.string.feature_stationdetail_text_company_contract),
            url = "https://metan.by/services/contracts/9894/",
        )
    }
}

@Composable
internal fun StationDetailPhotos(
    image: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(vertical = 12.dp)) {
        ItemDetailImageView(
            imageUrl = image,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(MMShapes.extraLarge)
        )
    }
}

@Composable
internal fun StationDetailAbout(modifier: Modifier = Modifier) {
    Column(modifier.padding(vertical = 12.dp)) {
        StationInfoRow(
            isExpandable = false,
            rowIcon = MMIcons.QuestionFilled,
            text = stringResource(R.string.feature_stationdetail_text_cng_about),
        )
    }
}