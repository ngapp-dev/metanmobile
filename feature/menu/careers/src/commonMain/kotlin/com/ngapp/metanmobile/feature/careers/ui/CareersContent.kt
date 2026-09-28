package com.ngapp.metanmobile.feature.careers.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.component.mmScrollContentPadding
import com.ngapp.metanmobile.core.model.career.CareerResource
import com.ngapp.metanmobile.core.ui.ads.NativeBanner
import com.ngapp.metanmobile.core.ui.ads.isNativeBannerSlot
import com.ngapp.metanmobile.core.ui.career.CareerRow
import com.ngapp.metanmobile.core.ui.career.CareerRowShimmer

/** A NativeBanner after every this many vacancies (their cards are tall). */
private const val NATIVE_BANNER_INTERVAL = 4

@Composable
internal fun CareersContent(
    modifier: Modifier = Modifier,
    staggeredGridState: LazyStaggeredGridState,
    careers: List<CareerResource>,
) {
    LazyVerticalStaggeredGrid(
        state = staggeredGridState,
        modifier = modifier
            .fillMaxSize()
            .animateContentSize()
            .testTag("careersScreen:feed"),
        columns = StaggeredGridCells.Adaptive(300.dp),
        contentPadding = mmScrollContentPadding().let { bars ->
            PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = bars.calculateTopPadding() + 16.dp,
                bottom = bars.calculateBottomPadding() + 16.dp,
            )
        },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
    ) {
        if (careers.isNotEmpty()) {
            careers.forEachIndexed { index, career ->
                item(key = career.id) {
                    CareerRow(
                        career = career,
                        cardElevation = 4.dp,
                    )
                }
                if (isNativeBannerSlot(index, NATIVE_BANNER_INTERVAL, careers.size)) {
                    item(key = "nativeBanner-$index", span = StaggeredGridItemSpan.FullLine) {
                        NativeBanner(slotKey = "careers-$index")
                    }
                }
            }
        } else {
            items(10) {
                CareerRowShimmer(cardElevation = 4.dp)
            }
        }
    }
}
