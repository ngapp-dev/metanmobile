package com.ngapp.metanmobile.feature.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.component.MMTextButton
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.ui.logNewsResourceOpened
import com.ngapp.metanmobile.core.ui.news.NewsRow
import com.ngapp.metanmobile.core.ui.news.PinnedNewsScreen
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun HomeHeaderView(
    isEditingUi: Boolean,
    isLastNewsExpended: Boolean,
    lastNewsItems: List<UserNewsResource>,
    pinnedNews: List<UserNewsResource>,
    modifier: Modifier = Modifier,
    onShowAllNewsClick: () -> Unit = {},
    onNewsDetailClick: (String) -> Unit = {},
    onExpandLastNewsClick: (Boolean) -> Unit,
) {
    val analyticsHelper = LocalAnalyticsHelper.current

    Column(
        // No shadow: this card sits right under the glass top bar, where its top shadow edge read
        // as a grey divider line.
        modifier = modifier
            .fillMaxWidth()
            .background(color = MMColors.cardBackgroundColor)
    ) {
        if (pinnedNews.isNotEmpty()) {
            PinnedNewsScreen(pinnedNews, onNewsDetailClick)
        }
        if (lastNewsItems.isNotEmpty()) {
            AnimatedVisibility(
                visible = isLastNewsExpended,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(SharedRes.strings.core_ui_title_latest_news),
                            style = MMTypography.displayMedium,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )
                        MMTextButton(
                            onClick = onShowAllNewsClick,
                            text = {
                                Text(
                                    text = stringResource(SharedRes.strings.core_ui_button_show_all),
                                    textAlign = TextAlign.End,
                                    style = MMTypography.headlineMedium,
                                    color = Gray400
                                )
                            }
                        )
                    }
                    lastNewsItems.forEachIndexed { i, news ->
                        NewsRow(
                            news = news,
                            titleMaxLines = 1,
                            imageSize = 64.dp,
                            imageRoundedCornerShape = RoundedCornerShape(12.dp, 0.dp, 12.dp, 0.dp),
                            onDetailClick = {
                                analyticsHelper.logNewsResourceOpened(newsId = news.id)
                                onNewsDetailClick(news.id)
                            }
                        )
                        if (i < lastNewsItems.size - 1) {
                            MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isEditingUi,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            val iconRotateState by animateFloatAsState(
                targetValue = if (isLastNewsExpended) 180f else 0f, label = "arrowAnimation"
            )
            IconButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onExpandLastNewsClick(!isLastNewsExpended) },
            ) {
                Icon(
                    modifier = Modifier.rotate(iconRotateState),
                    imageVector = MMIcons.ExpandMore,
                    contentDescription = stringResource(SharedRes.strings.core_ui_description_expand_news_icon),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}
