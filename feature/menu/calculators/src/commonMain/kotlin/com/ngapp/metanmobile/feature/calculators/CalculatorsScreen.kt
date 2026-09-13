package com.ngapp.metanmobile.feature.calculators

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMToolbarWithNavIcon
import com.ngapp.metanmobile.core.designsystem.theme.MMColors
import com.ngapp.metanmobile.core.designsystem.theme.textColor
import com.ngapp.metanmobile.core.ui.MetanMobileCalculators
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent

@Composable
fun CalculatorsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CalculatorsScreen(
        modifier = modifier,
        onBackClick = onBackClick
    )
}

@Composable
private fun CalculatorsScreen(
    modifier: Modifier,
    onBackClick: () -> Unit,
) {
    CalculatorsHeader(
        modifier = modifier,
        onBackClick = onBackClick
    ) { padding ->
        MetanMobileCalculators(
            modifier = Modifier.padding(padding),
            tabRowIndicatorColor = MMColors.textColor,
            tabNameColor = MMColors.textColor
        )
    }
    TrackScreenViewEvent(screenName = "CalculatorsScreen")
}

@Composable
private fun CalculatorsHeader(
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
                titleRes = SharedRes.strings.feature_menu_calculators_toolbar_title,
                onNavigationClick = onBackClick,
            )
        },
        content = pageContent
    )
}
