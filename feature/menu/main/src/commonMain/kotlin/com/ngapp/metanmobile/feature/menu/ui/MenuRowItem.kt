package com.ngapp.metanmobile.feature.menu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun MenuRowItem(
    modifier: Modifier = Modifier,
    title: StringResource,
    onPageItemClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MMColors.cardBackgroundColor)
            .clickable(onClick = onPageItemClick)
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(title),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(vertical = 4.dp),
            style = MMTypography.titleLarge
        )
        Icon(
            imageVector = MMIcons.KeyboardArrowRight,
            contentDescription = stringResource(SharedRes.strings.core_ui_description_settings_arrow_icon),
            tint = Gray400,
            modifier = Modifier.wrapContentSize()
        )
    }
}
