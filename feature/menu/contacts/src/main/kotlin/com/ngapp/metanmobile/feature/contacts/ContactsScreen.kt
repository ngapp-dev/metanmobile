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

package com.ngapp.metanmobile.feature.contacts

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMOverlayLoadingWheel
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.component.htmltext.HtmlText
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.model.contact.ContactResource
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.lottie.LottieEmptyView
import com.ngapp.metanmobile.feature.contacts.state.ContactsUiState

@Composable
internal fun ContactsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    ContactsScreen(
        modifier = modifier,
        uiState = uiState,
        isSyncing = isSyncing,
        onBackClick = onBackClick
    )
}

@Composable
private fun ContactsScreen(
    modifier: Modifier,
    uiState: ContactsUiState,
    isSyncing: Boolean,
    onBackClick: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val isLoading = uiState is ContactsUiState.Loading
    ReportDrawnWhen { !isSyncing && !isLoading }

    ContactsHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->

        Box(modifier = modifier.fillMaxSize()) {
            when (uiState) {
                ContactsUiState.Loading -> Unit
                is ContactsUiState.Success -> {
                    if (uiState.contact != null) {
                        Column(
                            modifier = modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(horizontal = 16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            HtmlText(
                                text = uiState.contact.content,
                                modifier = Modifier.padding(top = 16.dp),
                                onLinkClick = {
                                    uriHandler.openUri(it.replace("https://metan.by", ""))
                                },
                                flags = HtmlCompat.FROM_HTML_MODE_LEGACY,
                                style = MMTypography.titleLarge,
                            )
                        }
                    } else {
                        LottieEmptyView(
                            modifier = modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            message = stringResource(R.string.feature_menu_contacts_text_empty)
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isSyncing || isLoading,
            enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
        ) {
            val loadingContentDescription = "Contacts screen loading wheel"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                MMOverlayLoadingWheel(
                    modifier = Modifier.align(Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
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
                titleResId = R.string.feature_menu_contacts_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Composable
private fun ContactsScreenPreview() {
    MMTheme {
        ContactsScreen(
            modifier = Modifier,
            uiState = ContactsUiState.Success(ContactResource.init()),
            isSyncing = false,
            onBackClick = {}
        )
    }
}