package com.ngapp.metanmobile.feature.favorites.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.ButtonWithIcon
import com.ngapp.metanmobile.core.designsystem.component.MMTextButton
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.model.station.UserStationResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun FavoritesBottomSheetContent(
    station: UserStationResource,
    onApprove: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(MMColors.secondary)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.7f)) {
                Text(
                    text = stringResource(SharedRes.strings.feature_favorites_text_favor_delete),
                    modifier = Modifier.padding(start = 16.dp),
                    color = Blue,
                    style = MMTypography.displaySmall
                )
                Text(
                    text = station.title,
                    modifier = Modifier.padding(start = 16.dp),
                    style = MMTypography.headlineMedium
                )
            }
            MMTextButton(
                onClick = onCancel,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(0.3f),
                contentPadding = PaddingValues(
                    start = 6.dp,
                    top = 0.dp,
                    end = 10.dp,
                    bottom = 0.dp
                ),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = MMIcons.CancelFilled,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            tint = Blue
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            text = stringResource(SharedRes.strings.core_ui_button_cancel),
                            textAlign = TextAlign.End,
                            style = MMTypography.titleLarge,
                        )
                    }
                }
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
        Text(
            text = stringResource(
                SharedRes.strings.feature_favorites_text_delete_favor_description,
                station.title
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            style = MMTypography.bodyLarge
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
        ButtonWithIcon(
            onClick = { onApprove() },
            icon = MMIcons.DeleteFilled,
            iconTint = White,
            textRes = SharedRes.strings.feature_favorites_text_approve_remove,
            buttonBackgroundColor = Blue,
            fontColor = White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        )
    }
}
