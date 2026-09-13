package com.ngapp.metanmobile.feature.stationdetail.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.ButtonWithIcon
import com.ngapp.metanmobile.core.designsystem.component.MMFilledIconButton
import com.ngapp.metanmobile.core.designsystem.component.htmltext.HtmlText
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Black
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray500
import com.ngapp.metanmobile.core.designsystem.theme.Green
import com.ngapp.metanmobile.core.designsystem.theme.LightBlue
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import dev.icerock.moko.resources.compose.stringResource
import kotlin.math.roundToInt

@Composable
internal fun HeaderWithStatusAndActions(
    title: String,
    modifier: Modifier = Modifier,
    onShareClick: () -> Unit,
    onCloseClick: () -> Unit,
    stationStatus: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MMTypography.displayMedium.copy(
                    letterSpacing = (-0.5).sp,
                    fontWeight = FontWeight.Normal
                ),
            )
            stationStatus()
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(end = 4.dp)
        ) {
            MMFilledIconButton(
                modifier = Modifier.size(32.dp),
                iconModifier = Modifier.size(18.dp),
                imageVector = MMIcons.Share,
                contentDescription = stringResource(SharedRes.strings.feature_stationdetail_description_share_station),
                onClick = onShareClick,
            )
            MMFilledIconButton(
                modifier = Modifier.size(32.dp),
                iconModifier = Modifier.size(18.dp),
                imageVector = MMIcons.Close,
                contentDescription = stringResource(SharedRes.strings.designsystem_description_back),
                onClick = onCloseClick,
            )
        }
    }
}

@Composable
internal fun HeaderObjectType(
    objectType: String,
    distanceBetween: Double?,
    style: TextStyle = MMTypography.bodyLarge,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = objectType,
            style = style,
        )
        if (distanceBetween != null) {
            val tenths = (distanceBetween * 10).roundToInt()
            val km = "${tenths / 10},${tenths % 10}"
            Text(
                text = "·",
                style = style,
            )
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = MMIcons.CarOutlined,
                contentDescription = stringResource(SharedRes.strings.designsystem_description_car_icon),
                tint = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = stringResource(SharedRes.strings.core_ui_text_value_km, km),
                style = style,
            )
        }
    }
}

@Composable
internal fun HeaderObjectWorkTime(workingTime: String) {
    val is24Hours = workingTime.contains("круглосуточно")
    val displayText =
        if (is24Hours) stringResource(SharedRes.strings.core_ui_text_open_24_hours) else workingTime
    HtmlText(
        text = displayText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MMTypography.titleLarge,
        color = Green,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
internal fun HeaderObjectCNGPrice(cngPrice: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = MMIcons.GasStationOutlined,
            contentDescription = stringResource(SharedRes.strings.designsystem_description_gas_station_icon),
            tint = MaterialTheme.colorScheme.onPrimary,
        )
        Text(
            text = stringResource(SharedRes.strings.core_ui_text_byn_per_one_meter, cngPrice),
            style = MMTypography.bodyLarge,
        )
    }
}

@Composable
internal fun HeaderButtons(
    isFavorite: Boolean,
    onDirectionsClick: () -> Unit,
    onCallClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShareClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ButtonWithIcon(
            imageVector = MMIcons.DirectionsFilled,
            textRes = SharedRes.strings.feature_stationdetail_button_directions,
            containerColor = Blue,
            contentColor = White,
            shape = ShapeDefaults.ExtraLarge,
            onClick = { onDirectionsClick() }
        )
        ButtonWithIcon(
            imageVector = MMIcons.CallFilled,
            textRes = SharedRes.strings.feature_stationdetail_button_call,
            containerColor = LightBlue,
            contentColor = Blue,
            shape = ShapeDefaults.ExtraLarge,
            onClick = { onCallClick() }
        )
        ButtonWithIcon(
            imageVector = if (isFavorite) MMIcons.Bookmark else MMIcons.BookmarkBorder,
            textRes = if (isFavorite) SharedRes.strings.feature_stationdetail_button_saved else SharedRes.strings.feature_stationdetail_button_save,
            containerColor = if (isFavorite) LightBlue.copy(alpha = 0.8f)
                .compositeOver(Gray500) else LightBlue,
            contentColor = if (isFavorite) Black else Blue,
            shape = ShapeDefaults.ExtraLarge,
            onClick = { onToggleBookmark() }
        )
        ButtonWithIcon(
            imageVector = MMIcons.Share,
            textRes = SharedRes.strings.feature_stationdetail_button_share,
            containerColor = LightBlue,
            contentColor = Blue,
            shape = ShapeDefaults.ExtraLarge,
            onClick = { onShareClick() }
        )
    }
}
