/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ngapp.metanmobile.feature.onboarding

import android.annotation.SuppressLint
import androidx.activity.compose.ReportDrawnWhen
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMButton
import com.ngapp.metanmobile.core.designsystem.component.MMOnboardingTopAppBar
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray300
import com.ngapp.metanmobile.core.designsystem.theme.MMShapes
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.feature.onboarding.state.OnboardingAction
import com.ngapp.metanmobile.feature.onboarding.state.OnboardingUiState
import kotlinx.coroutines.launch

@Composable
internal fun OnboardingRoute(
    onSkipOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (uiState) {
        OnboardingUiState.NotShown -> onSkipOnboarding()
        else -> {
            OnboardingScreen(
                modifier = modifier,
                uiState = uiState,
                onAction = viewModel::triggerAction,
            )
        }
    }
}

@Composable
private fun OnboardingScreen(
    modifier: Modifier,
    uiState: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit,
) {
    val isLoading = uiState is OnboardingUiState.Loading

    val pages = listOf(
        OnBoardingPage.First,
        OnBoardingPage.Second,
        OnBoardingPage.Third
    )
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })

    ReportDrawnWhen { !isLoading }
    OnboardingHeader(
        modifier = modifier,
        shouldShowNavigationButton = pagerState.currentPage > 0,
        onBackClick = {
            if (pagerState.currentPage > 0) {
                scope.launch { pagerState.scrollToPage(pagerState.currentPage - 1) }
            }
        },
        onSkipClick = {
            scope.launch { pagerState.scrollToPage(pages.lastIndex) }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                PagerScreen(onBoardingPage = pages[page])
            }
            BottomSection(size = pages.size, index = pagerState.currentPage) {
                if (pagerState.currentPage < pages.lastIndex) {
                    scope.launch { pagerState.scrollToPage(pagerState.currentPage + 1) }
                } else {
                    onAction(OnboardingAction.DismissOnboarding)
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "OnboardingScreen")
}

@SuppressLint("DesignSystem")
@Composable
private fun BottomSection(
    size: Int,
    index: Int,
    onButtonClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(size) { Indicator(isSelected = it == index) }
        }
        Button(
            onClick = onButtonClick,
            colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = White),
            shape = MMShapes.large,
            modifier = Modifier.height(56.dp)
        ) {
            AnimatedVisibility(visible = index != 2) {
                Icon(
                    imageVector = MMIcons.KeyboardArrowRight,
                    tint = White,
                    contentDescription = stringResource(R.string.feature_onboarding_description_next_icon)
                )
            }
            AnimatedVisibility(visible = index == 2) {
                Text(
                    text = stringResource(id = R.string.feature_onboarding_button_get_start),
                    color = White,
                    textAlign = TextAlign.Center,
                    style = MMTypography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 64.dp)
                )
            }
        }
    }
}

@Composable
private fun Indicator(isSelected: Boolean) {
    val width = animateDpAsState(
        targetValue = if (isSelected) 25.dp else 10.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = stringResource(R.string.feature_onboarding_description_pager_indicator)
    )
    Box(
        modifier = Modifier
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(color = if (isSelected) Blue else Gray300)
    )
}

@Composable
private fun PagerScreen(onBoardingPage: OnBoardingPage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 50.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(onBoardingPage.image),
            contentDescription = stringResource(onBoardingPage.title),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .weight(4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(onBoardingPage.title),
            style = MMTypography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(onBoardingPage.description),
            style = MMTypography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(2f)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

private sealed class OnBoardingPage(
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    @StringRes val description: Int,
) {
    data object First : OnBoardingPage(
        image = R.drawable.feature_onboarding_onboarding_news,
        title = R.string.feature_onboarding_title_news,
        description = R.string.feature_onboarding_description_news
    )

    data object Second : OnBoardingPage(
        image = R.drawable.feature_onboarding_onboarding_stations,
        title = R.string.feature_onboarding_title_stations,
        description = R.string.feature_onboarding_description_stations
    )

    data object Third : OnBoardingPage(
        image = R.drawable.feature_onboarding_onboarding_favorites,
        title = R.string.feature_onboarding_title_favorites,
        description = R.string.feature_onboarding_description_favorites
    )
}

@Composable
private fun OnboardingHeader(
    modifier: Modifier,
    shouldShowNavigationButton: Boolean,
    onBackClick: () -> Unit,
    onSkipClick: () -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MMOnboardingTopAppBar(
                shouldShowNavigationButton = shouldShowNavigationButton,
                onNavigationClick = onBackClick,
                onSkipActionClick = onSkipClick
            )
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Composable
private fun OnboardScreenPreview() {
    MMTheme {
        OnboardingScreen(
            modifier = Modifier,
            uiState = OnboardingUiState.Shown,
            onAction = {}
        )
    }
}