package com.ngapp.metanmobile.feature.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.ui.MetanMobileCalculators
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun HomeWidgetCalculatorsView(
    isEditingUi: Boolean,
    modifier: Modifier = Modifier,
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Blue)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(SharedRes.strings.core_ui_title_calculate_profit),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = White,
                style = MMTypography.displayLarge,
            )
            MetanMobileCalculators(
                tabRowIndicatorColor = White,
                tabNameColor = White,
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
                    contentDescription = "Calculators widget ${stringResource(SharedRes.strings.core_ui_description_reorder_drag_handle_icon)}",
                    tint = White,
                )
            }
        }
    }
}
