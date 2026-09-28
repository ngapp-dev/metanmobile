package com.ngapp.metanmobile.feature.termsandconditions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMScaffold
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.model.userdata.LanguageConfig
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.util.LanguageHelper

private const val urlEn = "https://metanmobile.pages.dev/termsandconditions"
private const val urlRu = "https://metanmobile.pages.dev/termsandconditions_ru"

@Composable
fun TermsAndConditionsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    val languageHelper = remember { LanguageHelper() }
    val currentLanguage = languageHelper.getLanguageCode().uppercase()

    TermsAndConditionsHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val url = when (currentLanguage) {
                LanguageConfig.RU.name, LanguageConfig.BE.name -> urlRu
                else -> urlEn
            }
            // captureBackPresses (default true) already routes the system back gesture into the
            // WebView's own history, matching master's manual BackHandler+canGoBack.
            WebView(
                state = rememberWebViewState(url = url),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
    TrackScreenViewEvent(screenName = "TermsAndConditionsScreen")
}

@Composable
private fun TermsAndConditionsHeader(
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
                titleRes = SharedRes.strings.feature_menu_legalregulations_termsandconditions_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}
