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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
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
import com.ngapp.metanmobile.core.ui.stations.StationInfoRow
import com.ngapp.metanmobile.core.ui.stations.StationWorkTimeRow
import dev.icerock.moko.resources.compose.stringResource

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
            text = stringResource(SharedRes.strings.core_ui_text_no_news),
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
            text = stringResource(SharedRes.strings.feature_stationdetail_text_individual_contract),
            url = "https://metan.by/services/contracts/9895/",
        )
        StationInfoRow(
            isExpandable = false,
            rowIcon = MMIcons.ContractOutlined,
            text = stringResource(SharedRes.strings.feature_stationdetail_text_company_contract),
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
            text = stringResource(SharedRes.strings.feature_stationdetail_text_cng_about),
        )
    }
}
