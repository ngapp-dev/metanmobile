package com.ngapp.metanmobile.feature.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.common.util.shortFormatUnixDataToString
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.model.price.PriceResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.WidgetPriceAndLocationView
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Clock

@Composable
internal fun HomeWidgetUserLocationView(
    modifier: Modifier = Modifier,
    isEditingUi: Boolean,
    nearestStation: UserStationResource?,
    cngPrice: PriceResource?,
    onStationDetailClick: (String) -> Unit = {},
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Blue)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(SharedRes.strings.feature_home_text_hello_user),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = White,
                style = MMTypography.displayMedium,
            )
            Text(
                text = stringResource(
                    SharedRes.strings.feature_home_text_today_date,
                    shortFormatUnixDataToString(Clock.System.now().toEpochMilliseconds() / 1000)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
                color = White,
                style = MMTypography.displaySmall
            )
            Spacer(Modifier.height(12.dp))
            WidgetPriceAndLocationView(
                nearestStation = nearestStation,
                cngPrice = cngPrice,
                onStationDetailClick = onStationDetailClick,
            )
        }
        AnimatedVisibility(
            visible = isEditingUi,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd),
        ) {
            IconButton(
                modifier = modifier.padding(start = 8.dp, top = 8.dp),
                onClick = {},
            ) {
                Icon(
                    imageVector = MMIcons.DragHandle,
                    contentDescription = "User location widget ${stringResource(SharedRes.strings.core_ui_description_reorder_drag_handle_icon)}",
                    tint = White,
                )
            }
        }
    }
}
