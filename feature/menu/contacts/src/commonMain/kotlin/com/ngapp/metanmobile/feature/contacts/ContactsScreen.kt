package com.ngapp.metanmobile.feature.contacts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMLinearWavyProgressIndicator
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.component.htmltext.HtmlText
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.animation.EmptyView
import com.ngapp.metanmobile.feature.contacts.state.ContactsUiState
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ContactsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncFailed by viewModel.syncFailed.collectAsStateWithLifecycle()

    ContactsScreen(
        modifier = modifier,
        uiState = uiState,
        isSyncing = isSyncing,
        syncFailed = syncFailed,
        onBackClick = onBackClick,
        onRetryClick = viewModel::retrySync,
    )
}

@Composable
private fun ContactsScreen(
    modifier: Modifier,
    uiState: ContactsUiState,
    isSyncing: Boolean,
    syncFailed: Boolean,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit = {},
) {
    val uriHandler = LocalUriHandler.current
    val isLoading = uiState is ContactsUiState.Loading

    ContactsHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        )
        {
            AnimatedVisibility(
                visible = isSyncing || isLoading,
                enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
            ) {
                MMLinearWavyProgressIndicator()
            }
            when (uiState) {
                ContactsUiState.Loading -> Unit
                is ContactsUiState.Success -> {
                    if (uiState.contact != null) {
                        Column(
                            modifier = modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            HtmlText(
                                text = uiState.contact.content,
                                modifier = Modifier.padding(top = 16.dp),
                                onLinkClick = {
                                    uriHandler.openUri(it.replace("https://metan.by", ""))
                                },
                                style = MMTypography.titleLarge,
                            )
                        }
                    } else {
                        EmptyView(
                            modifier = modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            message = stringResource(SharedRes.strings.feature_menu_contacts_text_empty),
                            canRetry = syncFailed,
                            onClickRetry = onRetryClick,
                        )
                    }
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "ContactsScreen")
}

@Composable
private fun ContactsHeader(
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
                titleRes = SharedRes.strings.feature_menu_contacts_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}
