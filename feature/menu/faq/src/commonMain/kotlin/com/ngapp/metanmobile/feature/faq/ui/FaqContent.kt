package com.ngapp.metanmobile.feature.faq.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.faq.FaqResource
import com.ngapp.metanmobile.core.ui.faq.FaqRow
import com.ngapp.metanmobile.core.ui.faq.FaqRowShimmer

@Composable
internal fun FaqContent(
    modifier: Modifier = Modifier,
    faqList: List<FaqResource>,
) {
    LazyColumn(modifier = modifier) {
        if (faqList.isNotEmpty()) {
            itemsIndexed(items = faqList, key = { _, faq -> faq.id }) { i, faqItem ->
                Column {
                    FaqRow(
                        faqItem = faqItem,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MMColors.cardBackgroundColor)
                    )
                    if (i < faqList.size - 1) {
                        MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        } else {
            items(10) {
                Column {
                    FaqRowShimmer()
                    MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}
