package com.ngapp.metanmobile.feature.legalregulations.navigation
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.ngapp.metanmobile.feature.legalregulations.LegalRegulationsRoute
import kotlinx.serialization.Serializable
@Serializable data object LegalRegulationsNavigation
fun NavController.navigateToLegalRegulations(options: NavOptionsBuilder.() -> Unit = {}) = navigate(LegalRegulationsNavigation, options)
fun NavGraphBuilder.legalRegulationsScreen(
    onTermsAndConditionsPageClick: () -> Unit,
    onPrivacyPolicyPageClick: () -> Unit,
    onLocationInformationPageClick: () -> Unit,
    onBackClick: () -> Unit,
) = composable<LegalRegulationsNavigation> {
    LegalRegulationsRoute(
        onTermsAndConditionsPageClick,
        onPrivacyPolicyPageClick,
        onLocationInformationPageClick,
        onBackClick,
    )
}
