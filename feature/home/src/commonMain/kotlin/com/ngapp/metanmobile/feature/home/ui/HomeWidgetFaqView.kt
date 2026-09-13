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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.component.MMTextButton
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Green
import com.ngapp.metanmobile.core.designsystem.theme.MMShapes
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.ui.faq.FaqRow
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun HomeWidgetFaqView(
    isEditingUi: Boolean,
    pinnedFaqItems: List<FaqResource>,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Green)
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(SharedRes.strings.core_ui_text_faq),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = White,
                style = MMTypography.displayLarge,
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = MMShapes.large)
                    .background(MaterialTheme.colorScheme.onBackground)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    pinnedFaqItems.forEachIndexed { i, faq ->
                        FaqRow(faqItem = faq)
                        if (i < pinnedFaqItems.size - 1) {
                            MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            MMTextButton(
                onClick = onSeeAllClick,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                ),
                text = {
                    Text(
                        text = stringResource(SharedRes.strings.core_ui_button_show_all).uppercase(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        style = MMTypography.displaySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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
                    contentDescription = "Faq widget${stringResource(SharedRes.strings.core_ui_description_reorder_drag_handle_icon)}",
                    tint = White
                )
            }
        }
    }
}
