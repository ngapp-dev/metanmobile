package com.ngapp.metanmobile.feature.privacypolicy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMScaffold
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.util.LanguageHelper
import com.ngapp.metanmobile.feature.privacypolicy.state.PrivacyPolicyAction
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val urlEn = "https://metan.by/upload/metanmobile/privacypolicy.html"
private const val urlRu = "https://metan.by/upload/metanmobile/privacypolicy_ru.html"

@Composable
fun PrivacyPolicyRoute(
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

@Composable
private fun PrivacyPolicyScreen(
    modifier: Modifier,
    isPrivacyOptionsRequired: Boolean,
    onUpdateConsent: () -> Unit,
    onBackClick: () -> Unit,
) {
    val languageHelper = remember { LanguageHelper() }
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

                if (isPrivacyOptionsRequired) {
                    ConsentChangeLink(onUpdateConsent)
                }

                // captureBackPresses (default true) already routes the system back gesture into
                // the WebView's own history, matching master's manual BackHandler+canGoBack.
                WebView(
                    state = rememberWebViewState(url = url),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
    TrackScreenViewEvent(screenName = "PrivacyPolicyScreen")
}

@Composable
private fun ConsentChangeLink(onUpdateConsent: () -> Unit) {
    Text(
        buildAnnotatedString {
            append(stringResource(SharedRes.strings.core_ui_change_consent))
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
                append(stringResource(SharedRes.strings.core_ui_here))
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
    MMScaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MMToolbarWithNavIcon(
                titleRes = SharedRes.strings.feature_menu_legalregulations_privacypolicy_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}
