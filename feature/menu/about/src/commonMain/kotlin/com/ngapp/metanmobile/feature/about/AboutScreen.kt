package com.ngapp.metanmobile.feature.about

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMAsyncImage
import com.ngapp.metanmobile.core.designsystem.component.MMLinearWavyProgressIndicator
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.feature.about.state.AboutUiState
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AboutRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AboutViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    AboutScreen(
        modifier = modifier,
        isSyncing = isSyncing,
        uiState = uiState,
        onBackClick = onBackClick
    )
}

@Composable
private fun AboutScreen(
    modifier: Modifier,
    isSyncing: Boolean,
    uiState: AboutUiState,
    onBackClick: () -> Unit,
) {
    val isLoading = uiState is AboutUiState.Loading
    val uriHandler = LocalUriHandler.current

    AboutHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(
                visible = isSyncing || isLoading,
                enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
            ) {
                MMLinearWavyProgressIndicator()
            }
            when (uiState) {
                is AboutUiState.Loading -> Unit
                is AboutUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MMAsyncImage(
                            imageUrl = uiState.githubUser?.avatarUrl,
                            contentDescription = uiState.githubUser?.name
                                ?: stringResource(SharedRes.strings.feature_menu_about_text_ngapps_dev),
                        )
                        Spacer(Modifier.height(18.dp))
                        Text(
                            text = stringResource(SharedRes.strings.feature_menu_about_text_developed_by),
                            style = MMTypography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = uiState.githubUser?.name
                                ?: stringResource(SharedRes.strings.feature_menu_about_text_ngapps_dev),
                            style = MMTypography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(SharedRes.strings.feature_menu_about_text_android_developer),
                            style = MMTypography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = uiState.githubUser?.htmlUrl.orEmpty(),
                            style = MMTypography.titleLarge,
                            modifier = Modifier
                                .clickable(uiState.githubUser?.htmlUrl.isNullOrEmpty().not()) {
                                    uriHandler.openUri(uiState.githubUser?.htmlUrl.orEmpty())
                                }
                        )
                        Spacer(Modifier.height(18.dp))
                        Text(
                            text = stringResource(SharedRes.strings.feature_menu_about_text_copyright_explanation),
                            style = MMTypography.headlineMedium,
                        )
                    }
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "AboutScreen")
}

@Composable
private fun AboutHeader(
    modifier: Modifier,
    onBackClick: () -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MMToolbarWithNavIcon(
                titleRes = SharedRes.strings.feature_menu_about_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}
