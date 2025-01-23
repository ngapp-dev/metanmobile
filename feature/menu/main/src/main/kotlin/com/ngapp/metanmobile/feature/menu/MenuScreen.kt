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

package com.ngapp.metanmobile.feature.menu

import android.os.Build
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.core.analytics.LocalAnalyticsHelper
import com.ngapp.metanmobile.core.data.repository.logLanguageConfigChanged
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.component.MMMenuTopAppBar
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.util.LanguageHelper
import com.ngapp.metanmobile.feature.menu.state.SettingsAction
import com.ngapp.metanmobile.feature.menu.state.SettingsUiState
import com.ngapp.metanmobile.feature.menu.ui.LanguageConfigDialog
import com.ngapp.metanmobile.feature.menu.ui.LanguageConfigRowItem
import com.ngapp.metanmobile.feature.menu.ui.LegalRegulationsRowItem
import com.ngapp.metanmobile.feature.menu.ui.MenuRowItem
import com.ngapp.metanmobile.feature.menu.ui.ThemeModeConfigDialog
import com.ngapp.metanmobile.feature.menu.ui.ThemeModeRowItem

@Composable
internal fun MenuRoute(
    onContactsPageClick: () -> Unit,
    onFaqPageClick: () -> Unit,
    onCalculatorsPageClick: () -> Unit,
    onAboutPageClick: () -> Unit,
    onLegalRegulationsPageClick: () -> Unit,
    onCareerPageClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = hiltViewModel(),
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
    val languageHelper = LanguageHelper(LocalContext.current)
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
                    title = R.string.feature_menu_main_title_career,
                    onPageItemClick = onCareerPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                MenuRowItem(
                    title = R.string.feature_menu_main_title_calculators,
                    onPageItemClick = onCalculatorsPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                MenuRowItem(
                    title = R.string.feature_menu_main_title_contacts,
                    onPageItemClick = onContactsPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                MenuRowItem(
                    title = R.string.feature_menu_main_title_faq,
                    onPageItemClick = onFaqPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                if (uiState is SettingsUiState.Success) {
                    ThemeModeRowItem(
                        titleResId = R.string.feature_menu_main_title_theme_mode,
                        themeMode = uiState.darkThemeConfig,
                        onOpenAlertDialog = { showThemeModeDialog = true }
                    )
                }
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (uiState is SettingsUiState.Success) {
                        LanguageConfigRowItem(
                            titleResId = R.string.feature_menu_main_title_app_language,
                            currentLanguage = currentLanguage,
                            onShowAlertDialog = { showLanguageDialog = true }
                        )
                    }
                }
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                MenuRowItem(
                    title = R.string.feature_menu_main_title_about,
                    onPageItemClick = onAboutPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                LegalRegulationsRowItem(
                    title = R.string.feature_menu_main_title_legal_regulations,
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
                titleResId = R.string.feature_menu_main_toolbar_title,
                onNavigationClick = onBackClick,
                onSupportClick = onContactsPageClick
            )
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Preview
@Composable
private fun MenuScreenPreview() {
    MMTheme {
        MenuScreen(
            modifier = Modifier,
            uiState = SettingsUiState.Success(darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM),
            onAction = {},
            onContactsPageClick = {},
            onFaqPageClick = {},
            onCalculatorsPageClick = {},
            onAboutPageClick = {},
            onLegalRegulationsPageClick = {},
            onCareerPageClick = {},
            onBackClick = {}
        )
    }
}