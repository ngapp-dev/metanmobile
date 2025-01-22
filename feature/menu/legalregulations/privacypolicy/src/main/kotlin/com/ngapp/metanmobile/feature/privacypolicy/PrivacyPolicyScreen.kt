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

package com.ngapp.metanmobile.feature.privacypolicy

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.util.LanguageHelper
import com.ngapp.metanmobile.feature.privacypolicy.state.PrivacyPolicyAction
import com.ngapp.metanmobile.core.ui.R as CoreUiR

private const val urlEn = "https://metan.by/upload/metanmobile/privacypolicy.html"
private const val urlRu = "https://metan.by/upload/metanmobile/privacypolicy_ru.html"

@Composable
internal fun PrivacyPolicyRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PrivacyPolicyViewModel = hiltViewModel(),
) {
    val isPrivacyOptionsRequired by viewModel.isPrivacyOptionsRequired.collectAsStateWithLifecycle()

    PrivacyPolicyScreen(
        modifier = modifier,
        isPrivacyOptionsRequired = isPrivacyOptionsRequired,
        onUpdateConsent = { viewModel.triggerAction(PrivacyPolicyAction.UpdateConsent) },
        onBackClick = onBackClick
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PrivacyPolicyScreen(
    modifier: Modifier,
    isPrivacyOptionsRequired: Boolean,
    onUpdateConsent: () -> Unit,
    onBackClick: () -> Unit,
) {
    val languageHelper = LanguageHelper(LocalContext.current)
    val currentLanguage = languageHelper.getLanguageCode().uppercase()

    PrivacyPolicyHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column {
                val url = when (currentLanguage) {
                    LanguageConfig.RU.name, LanguageConfig.BE.name -> urlRu
                    else -> urlEn
                }
                var backEnabled by remember { mutableStateOf(false) }
                var webView: WebView? = null

                if (isPrivacyOptionsRequired) {
                    ConsentChangeLink(onUpdateConsent)
                }

                AndroidView(
                    modifier = modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(
                                    view: WebView,
                                    url: String?,
                                    favicon: Bitmap?,
                                ) {
                                    backEnabled = view.canGoBack()
                                }
                            }
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.cacheMode = WebSettings.LOAD_NO_CACHE

                            loadUrl(url)
                            webView = this
                        }
                    }, update = {
                        webView = it
                    })

                BackHandler(enabled = backEnabled) {
                    webView?.goBack()
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "PrivacyPolicyScreen")
}

@Composable
private fun ConsentChangeLink(onUpdateConsent: () -> Unit) {
    Text(
        buildAnnotatedString {
            append(stringResource(CoreUiR.string.core_ui_change_consent))
            withLink(
                LinkAnnotation.Clickable(
                    linkInteractionListener = { onUpdateConsent() },
                    styles = TextLinkStyles(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 0.25.sp,
                        )
                    ),
                    tag = ""
                )
            ) {
                append(stringResource(CoreUiR.string.core_ui_here))
            }
        },
        modifier = Modifier.padding(horizontal = 16.dp),
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun PrivacyPolicyHeader(
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
                titleResId = R.string.feature_menu_legalregulations_privacypolicy_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Composable
private fun PrivacyPolicyPreview() {
    MMTheme {
        PrivacyPolicyScreen(
            modifier = Modifier,
            isPrivacyOptionsRequired = true,
            onUpdateConsent = {},
            onBackClick = {},
        )
    }
}