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

package com.ngapp.metanmobile.feature.legalregulations

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.ngapp.metanmobile.core.designsystem.component.MMDivider
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTheme
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.feature.legalregulations.ui.ItemPageRow

@Composable
internal fun LegalRegulationsRoute(
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
        val context = LocalContext.current

        Surface(shadowElevation = 4.dp) {
            Column(
                modifier = modifier
                    .background(MMColors.cardBackgroundColor)
                    .wrapContentHeight()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
            ) {
                ItemPageRow(
                    title = R.string.feature_menu_legalregulations_main_title_terms_and_conditions,
                    onPageItemClick = onTermsAndConditionsPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ItemPageRow(
                    title = R.string.feature_menu_legalregulations_main_title_privacy_policy,
                    onPageItemClick = onPrivacyPolicyPageClick,
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ItemPageRow(
                    title = R.string.feature_menu_legalregulations_main_title_software_licence,
                    onPageItemClick = {
                        context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                    },
                )
                MMDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ItemPageRow(
                    title = R.string.feature_menu_legalregulations_main_title_location_information,
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
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MMToolbarWithNavIcon(
                titleResId = R.string.feature_menu_legalregulations_main_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}

@PreviewScreenSizes
@Preview
@Composable
private fun LegalRegulationsPreview() {
    MMTheme {
        LegalRegulationsScreen(
            modifier = Modifier,
            onTermsAndConditionsPageClick = {},
            onPrivacyPolicyPageClick = {},
            onLocationInformationPageClick = {},
            onBackClick = {}
        )
    }
}