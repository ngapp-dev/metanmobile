package com.ngapp.metanmobile.composeapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private data class OnboardingPage(val title: String, val description: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingPage("Добро пожаловать", "Metan Mobile объединяет цены, станции и новости в одном приложении."),
        OnboardingPage("Цены и станции", "Быстро находите ближайшие станции и актуальные цены на топливо."),
        OnboardingPage("Будьте в курсе", "Читайте новости и сохраняйте важные материалы в избранное."),
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(pages[page].title, style = MaterialTheme.typography.headlineLarge)
                Text(
                    pages[page].description,
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.indices.forEach { index ->
                    val color = if (index == pagerState.currentPage) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color)
                            .padding(5.dp),
                    )
                }
            }
            Button(onClick = {
                if (pagerState.currentPage == pages.lastIndex) {
                    onFinished()
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            }) {
                Text(if (pagerState.currentPage == pages.lastIndex) "Начать" else "Далее")
            }
        }
    }
}
