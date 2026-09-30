package com.ngapp.metanmobile.feature.menu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.ui.util.appVersion
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun LegalRegulationsRowItem(
    modifier: Modifier = Modifier,
    title: StringResource,
    onPageItemClick: () -> Unit,
) {
    val version = appVersion()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
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
        Text(
            text = version,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(vertical = 4.dp),
            style = MMTypography.titleLarge
        )
    }
}
