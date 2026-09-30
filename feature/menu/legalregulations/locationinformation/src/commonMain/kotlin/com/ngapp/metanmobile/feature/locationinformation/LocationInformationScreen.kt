package com.ngapp.metanmobile.feature.locationinformation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMScaffold
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.cardBackgroundColor
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun LocationInformationScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    LocationInformationHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->
        Column(
            modifier = modifier
                .background(MMColors.cardBackgroundColor)
                .wrapContentHeight()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(SharedRes.strings.feature_menu_legalregulations_locationinformation_title_how_do_we_use_location),
                modifier = Modifier.fillMaxWidth(),
                style = MMTypography.displayMedium
            )
            Text(
                text = stringResource(SharedRes.strings.feature_menu_legalregulations_locationinformation_text_location_explanation),
                modifier = Modifier.fillMaxWidth(),
                style = MMTypography.headlineMedium
            )
        }
    }
    TrackScreenViewEvent(screenName = "LocationInformationScreen")
}

@Composable
private fun LocationInformationHeader(
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
                titleRes = SharedRes.strings.feature_menu_legalregulations_locationinformation_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}
