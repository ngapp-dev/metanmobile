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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.util.LanguageHelper
import com.ngapp.metanmobile.feature.privacypolicy.state.PrivacyPolicyAction
import org.koin.compose.viewmodel.koinViewModel
import com.ngapp.metanmobile.core.ui.R as CoreUiR

private const val urlEn = "https://metan.by/upload/metanmobile/privacypolicy.html"
private const val urlRu = "https://metan.by/upload/metanmobile/privacypolicy_ru.html"

@Composable
internal fun PrivacyPolicyRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PrivacyPolicyViewModel = koinViewModel(),
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

/**
 * The privacy policy page rendered by the [AndroidView] below carries no styling of its own
 * beyond `body { padding: 8px }` (see privacypolicy.html/privacypolicy_ru.html) - it's always a
 * plain white page with black text and default blue underlined links, regardless of the app's
 * theme. This link sits directly above that page, so it's pinned to the same look instead of
 * [MaterialTheme]'s colors: in dark mode those would put light body text right on top of the
 * page's white background with no visual separation.
 */
@Composable
private fun ConsentChangeLink(onUpdateConsent: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
    ) {
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = Color.Black)) {
                    append(stringResource(CoreUiR.string.core_ui_change_consent))
                }
                withLink(
                    LinkAnnotation.Clickable(
                        linkInteractionListener = { onUpdateConsent() },
                        styles = TextLinkStyles(
                            SpanStyle(
                                color = HtmlLinkColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 0.25.sp,
                                textDecoration = TextDecoration.Underline,
                            )
                        ),
                        tag = ""
                    )
                ) {
                    append(stringResource(CoreUiR.string.core_ui_here))
                }
            },
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
        )
    }
}

/** The default, unstyled `<a>` link color a WebView renders - matches the page below. */
private val HtmlLinkColor = Color(0xFF0000EE)

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
