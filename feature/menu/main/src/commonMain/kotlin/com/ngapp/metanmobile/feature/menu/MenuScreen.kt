package com.ngapp.metanmobile.feature.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.data.repository.logLanguageConfigChanged
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.component.MMMenuTopAppBar
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.util.LanguageHelper
import com.ngapp.metanmobile.core.ui.util.isPerAppLanguageConfigSupported
import com.ngapp.metanmobile.core.ui.util.isSystemLanguageSettingsAvailable
import com.ngapp.metanmobile.core.ui.util.openAppSettings
import com.ngapp.metanmobile.feature.menu.state.SettingsAction
import com.ngapp.metanmobile.feature.menu.state.SettingsUiState
import com.ngapp.metanmobile.feature.menu.ui.LanguageConfigDialog
import com.ngapp.metanmobile.feature.menu.ui.LanguageConfigRowItem
import com.ngapp.metanmobile.feature.menu.ui.LegalRegulationsRowItem
import com.ngapp.metanmobile.feature.menu.ui.MenuRowItem
import com.ngapp.metanmobile.feature.menu.ui.ThemeModeConfigDialog
import com.ngapp.metanmobile.feature.menu.ui.ThemeModeRowItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MenuRoute(
    onContactsPageClick: () -> Unit,
    onFaqPageClick: () -> Unit,
    onCalculatorsPageClick: () -> Unit,
    onAboutPageClick: () -> Unit,
    onLegalRegulationsPageClick: () -> Unit,
    onCareerPageClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = koinViewModel(),
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    MenuScreen(
        modifier = modifier,
        uiState = settingsUiState,
        onAction = viewModel::triggerAction,
        onContactsPageClick = onContactsPageClick,
        onFaqPageClick = onFaqPageClick,
        onCalculatorsPageClick = onCalculatorsPageClick,
        onAboutPageClick = onAboutPageClick,
        onLegalRegulationsPageClick = onLegalRegulationsPageClick,
        onCareerPageClick = onCareerPageClick,
        onBackClick = onBackClick
    )
}

@Composable
private fun MenuScreen(
    modifier: Modifier,
    uiState: SettingsUiState,
    onAction: (SettingsAction) -> Unit,
    onContactsPageClick: () -> Unit,
    onFaqPageClick: () -> Unit,
    onCalculatorsPageClick: () -> Unit,
    onAboutPageClick: () -> Unit,
    onLegalRegulationsPageClick: () -> Unit,
    onCareerPageClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    var showThemeModeDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    val analyticsHelper = LocalAnalyticsHelper.current
    val languageHelper = remember { LanguageHelper() }
    val currentLanguage = languageHelper.getLanguageCode().uppercase()

    if (showThemeModeDialog && uiState is SettingsUiState.Success) {
        ThemeModeConfigDialog(
            darkThemeConfig = uiState.darkThemeConfig,
            onChangeDarkThemeConfig = { onAction(SettingsAction.UpdateDarkThemeConfig(it)) },
            onShowAlertDialog = { showThemeModeDialog = it }
        )
    }
    if (showLanguageDialog && uiState is SettingsUiState.Success) {
        LanguageConfigDialog(
            currentLanguage = currentLanguage,
            onChangeLanguageConfig = {
                languageHelper.changeLanguage(it)
                analyticsHelper.logLanguageConfigChanged(it)
            },
            onShowAlertDialog = { showLanguageDialog = it }
        )
    }

    MenuHeader(
        modifier = modifier,
        onContactsPageClick = onContactsPageClick,
        onBackClick = onBackClick
    ) { padding ->
        Surface(shadowElevation = 4.dp) {
            Column(
                modifier = modifier
                    .background(MMColors.cardBackgroundColor)
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
            ) {
                MenuRowItem(
                    title = SharedRes.strings.feature_menu_main_title_career,
                    onPageItemClick = onCareerPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                MenuRowItem(
                    title = SharedRes.strings.feature_menu_main_title_calculators,
                    onPageItemClick = onCalculatorsPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                MenuRowItem(
                    title = SharedRes.strings.feature_menu_main_title_contacts,
                    onPageItemClick = onContactsPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                MenuRowItem(
                    title = SharedRes.strings.feature_menu_main_title_faq,
                    onPageItemClick = onFaqPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                if (uiState is SettingsUiState.Success) {
                    ThemeModeRowItem(
                        titleRes = SharedRes.strings.feature_menu_main_title_theme_mode,
                        themeMode = uiState.darkThemeConfig,
                        onOpenAlertDialog = { showThemeModeDialog = true }
                    )
                }
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                if (isPerAppLanguageConfigSupported()) {
                    if (uiState is SettingsUiState.Success) {
                        LanguageConfigRowItem(
                            titleRes = SharedRes.strings.feature_menu_main_title_app_language,
                            currentLanguage = currentLanguage,
                            onShowAlertDialog = { showLanguageDialog = true }
                        )
                    }
                    MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                } else if (isSystemLanguageSettingsAvailable()) {
                    // No in-app language API (iOS) - route to this app's own page in the system
                    // Settings app instead, where CFBundleLocalizations (Info.plist) now makes a
                    // per-app "Language" row available.
                    LanguageConfigRowItem(
                        titleRes = SharedRes.strings.feature_menu_main_title_app_language,
                        currentLanguage = currentLanguage,
                        onShowAlertDialog = { openAppSettings() }
                    )
                    MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
                MenuRowItem(
                    title = SharedRes.strings.feature_menu_main_title_about,
                    onPageItemClick = onAboutPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                LegalRegulationsRowItem(
                    title = SharedRes.strings.feature_menu_main_title_legal_regulations,
                    onPageItemClick = onLegalRegulationsPageClick
                )
            }
        }
    }
    TrackScreenViewEvent(screenName = "MenuScreen")
}

@Composable
private fun MenuHeader(
    modifier: Modifier,
    onContactsPageClick: () -> Unit,
    onBackClick: () -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MMMenuTopAppBar(
                titleRes = SharedRes.strings.feature_menu_main_toolbar_title,
                onNavigationClick = onBackClick,
                onSupportClick = onContactsPageClick
            )
        },
        content = pageContent
    )
}
