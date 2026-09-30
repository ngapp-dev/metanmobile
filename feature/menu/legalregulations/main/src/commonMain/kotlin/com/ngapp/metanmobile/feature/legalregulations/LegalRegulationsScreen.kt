package com.ngapp.metanmobile.feature.legalregulations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.component.MMScaffold
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.util.openOssLicenses
import com.ngapp.metanmobile.feature.legalregulations.ui.ItemPageRow

@Composable
fun LegalRegulationsRoute(
    onTermsAndConditionsPageClick: () -> Unit,
    onPrivacyPolicyPageClick: () -> Unit,
    onLocationInformationPageClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LegalRegulationsScreen(
        modifier = modifier,
        onTermsAndConditionsPageClick = onTermsAndConditionsPageClick,
        onPrivacyPolicyPageClick = onPrivacyPolicyPageClick,
        onLocationInformationPageClick = onLocationInformationPageClick,
        onBackClick = onBackClick
    )
}

@Composable
private fun LegalRegulationsScreen(
    modifier: Modifier,
    onTermsAndConditionsPageClick: () -> Unit,
    onPrivacyPolicyPageClick: () -> Unit,
    onLocationInformationPageClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    LegalRegulationsHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->
        Surface(shadowElevation = 4.dp) {
            Column(
                modifier = modifier
                    .background(MMColors.cardBackgroundColor)
                    .wrapContentHeight()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
            ) {
                ItemPageRow(
                    title = SharedRes.strings.feature_menu_legalregulations_main_title_terms_and_conditions,
                    onPageItemClick = onTermsAndConditionsPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ItemPageRow(
                    title = SharedRes.strings.feature_menu_legalregulations_main_title_privacy_policy,
                    onPageItemClick = onPrivacyPolicyPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ItemPageRow(
                    title = SharedRes.strings.feature_menu_legalregulations_main_title_software_licence,
                    onPageItemClick = { openOssLicenses() },
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ItemPageRow(
                    title = SharedRes.strings.feature_menu_legalregulations_main_title_location_information,
                    onPageItemClick = onLocationInformationPageClick,
                )
            }
        }
    }
    TrackScreenViewEvent(screenName = "LegalRegulationsScreen")
}

@Composable
private fun LegalRegulationsHeader(
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
                titleRes = SharedRes.strings.feature_menu_legalregulations_main_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}
