package com.ngapp.metanmobile.composeapp.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.composeapp.DeepLinkHolder
import com.ngapp.metanmobile.composeapp.ui.MetanMobileAppState
import com.ngapp.metanmobile.core.designsystem.component.MMNavigationSuiteScaffold
import com.ngapp.metanmobile.core.designsystem.component.MetanMobileBackground
import com.ngapp.metanmobile.core.designsystem.component.MetanMobileGradientBackground
import com.ngapp.metanmobile.core.designsystem.theme.LocalGradientColors
import com.ngapp.metanmobile.core.ui.ads.ConsentGatedBannerAd
import com.ngapp.metanmobile.core.ui.ads.ConsentHelper
import com.ngapp.metanmobile.core.ui.ads.MainBannerAd
import com.ngapp.metanmobile.feature.about.navigation.aboutScreen
import com.ngapp.metanmobile.feature.about.navigation.navigateToAbout
import com.ngapp.metanmobile.feature.cabinet.navigation.cabinetScreen
import com.ngapp.metanmobile.feature.cabinet.navigation.navigateToCabinet
import com.ngapp.metanmobile.feature.calculators.navigation.calculatorsScreen
import com.ngapp.metanmobile.feature.calculators.navigation.navigateToCalculators
import com.ngapp.metanmobile.feature.careers.navigation.careersScreen
import com.ngapp.metanmobile.feature.careers.navigation.navigateToCareers
import com.ngapp.metanmobile.feature.contacts.navigation.contactsScreen
import com.ngapp.metanmobile.feature.contacts.navigation.navigateToContacts
import com.ngapp.metanmobile.feature.faq.navigation.faqScreen
import com.ngapp.metanmobile.feature.faq.navigation.navigateToFaq
import com.ngapp.metanmobile.feature.favorites.navigation.FavoritesScreen
import com.ngapp.metanmobile.feature.home.navigation.HomeScreen
import com.ngapp.metanmobile.feature.legalregulations.navigation.legalRegulationsScreen
import com.ngapp.metanmobile.feature.legalregulations.navigation.navigateToLegalRegulations
import com.ngapp.metanmobile.feature.locationinformation.navigation.locationInformationScreen
import com.ngapp.metanmobile.feature.locationinformation.navigation.navigateToLocationInformation
import com.ngapp.metanmobile.feature.menu.navigation.menuScreen
import com.ngapp.metanmobile.feature.menu.navigation.navigateToMenu
import com.ngapp.metanmobile.feature.news.navigation.NewsScreen
import com.ngapp.metanmobile.feature.news.navigation.navigateToNewsDetail
import com.ngapp.metanmobile.feature.news.navigation.newsDetailScreen
import com.ngapp.metanmobile.feature.onboarding.OnboardingViewModel
import com.ngapp.metanmobile.feature.onboarding.navigation.OnboardingNavigationRoute
import com.ngapp.metanmobile.feature.onboarding.navigation.onboardingScreen
import com.ngapp.metanmobile.feature.privacypolicy.navigation.navigateToPrivacyPolicy
import com.ngapp.metanmobile.feature.privacypolicy.navigation.privacyPolicyScreen
import com.ngapp.metanmobile.feature.stationdetail.navigation.navigateToStationDetail
import com.ngapp.metanmobile.feature.stationdetail.navigation.stationDetailScreen
import com.ngapp.metanmobile.feature.stations.navigation.StationsScreen
import com.ngapp.metanmobile.feature.termsandconditions.navigation.navigateToTermsAndConditions
import com.ngapp.metanmobile.feature.termsandconditions.navigation.termsAndConditionsScreen
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject

/** Shared Android/iOS graph. Features own their routes; this host only joins callbacks. */
@Composable
fun MetanMobileNavHost(
    initialOnboarding: Boolean,
    appState: MetanMobileAppState,
    onboardingViewModel: OnboardingViewModel,
) {
    // .value here (as opposed to collectAsStateWithLifecycle) never leaves uiState's
    // `initialValue = Loading`: stateIn(WhileSubscribed(5_000), ...) only starts collecting the
    // upstream userData flow once something actively subscribes, and a bare `.value` read never
    // does that — so it froze forever at Loading. With initialOnboarding hardcoded true at both
    // platform entry points, "not NotShown" (Loading counts as "not NotShown" too) was therefore
    // always true: onboarding showed on *every* launch, regardless of the persisted
    // shouldHideOnboarding flag. Collecting it properly here is the actual fix.
    val onboardingUiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()

    val navController = appState.navController

    // Route the launching/incoming URL (App Link tap on Android, Universal Link/custom-scheme
    // open on iOS) to the matching screen, and again for every later one while the app is already
    // running — mirrors master's MainActivity.LaunchedEffect(pendingIntent) { pendingIntent?.data
    // ?.let { appState.navigateToDeepLink(it) } }, just fed by DeepLinkHolder instead of an
    // Android-only Intent so both platform entry points can push into the same place.
    val pendingDeepLink by DeepLinkHolder.pendingUrl.collectAsStateWithLifecycle()
    LaunchedEffect(pendingDeepLink) {
        pendingDeepLink?.let {
            appState.navigateToDeepLink(it)
            DeepLinkHolder.consume()
        }
    }

    val entry by navController.currentBackStackEntryAsState()
    val isOnboarding = entry?.destination?.hasRoute<OnboardingNavigationRoute>() == true
    val selectedDestination = TopLevelDestination.entries.firstOrNull { entry?.destination?.route == it.route }

    val consentHelper = koinInject<ConsentHelper>()
    val canShowAds by consentHelper.canShowAds.collectAsStateWithLifecycle()

    MetanMobileBackground {
        MetanMobileGradientBackground(gradientColors = LocalGradientColors.current) {
            // Navigation-Compose locks NavHost's startDestination in at its first composition and
            // never re-evaluates it on a later value change, so the NavHost can't be mounted until
            // we actually know whether to show onboarding — otherwise it's stuck on whatever the
            // very first (often still-Loading) read produced, same bug as above. Master sidesteps
            // this with a splash screen that stays up until userData loads (MainActivity's
            // installSplashScreen + setKeepOnScreenCondition); here it's enough to just hold off
            // mounting the NavHost for that one frame — the backgrounds below still render, so
            // there's no blank/white flash, and a cold DataStore read is normally sub-frame fast.
            if (onboardingUiState !is com.ngapp.metanmobile.feature.onboarding.state.OnboardingUiState.Loading) {
                val showOnboarding =
                    initialOnboarding &&
                        onboardingUiState is com.ngapp.metanmobile.feature.onboarding.state.OnboardingUiState.Shown

                MMNavigationSuiteScaffold(
                    navigationSuiteItems = {
                        TopLevelDestination.entries.forEach { destination ->
                            val selected = destination == selectedDestination
                            item(
                                selected = selected,
                                onClick = { appState.navigateToTopLevelDestination(destination) },
                                icon = { Icon(destination.unselectedIcon, contentDescription = null) },
                                selectedIcon = { Icon(destination.selectedIcon, contentDescription = null) },
                                label = { Text(stringResource(destination.title)) },
                            )
                        }
                    },
                    showBottomBar = !isOnboarding && selectedDestination != null,
                    adsContent = { ConsentGatedBannerAd(canShowAds = canShowAds, bannerAd = { MainBannerAd() }) },
                ) {
                    // Split into two statically-typed NavHost calls instead of one call fed a
                    // shared `startDestination: Any` — with an Any-typed value, Kotlin resolves
                    // the single generic overload for *both* branches, but this graph mixes a
                    // String-registered destination (Home, via the classic `composable(route:
                    // String)`) with a Kotlin-Serialization/KClass one (Onboarding, via
                    // `composable<OnboardingNavigationRoute>()`); starting from a plain String in
                    // that generic path crashed with "Cannot find startDestination kotlin.String
                    // from NavGraph. Ensure the starting NavDestination was added with route from
                    // KClass." Each call below is statically typed to the destination's own
                    // registration style, so the compiler — and the library — never has to guess.
                    val graphContent: androidx.navigation.NavGraphBuilder.() -> Unit = {
                        onboardingScreen(appState::navigateFromOnboardingToHome)
                        composable(TopLevelDestination.HOME.route) {
                        HomeScreen(
                            onNewsClick = { appState.navigateToTopLevelDestination(TopLevelDestination.NEWS) },
                            onNewsDetailClick = navController::navigateToNewsDetail,
                            onFaqClick = navController::navigateToFaq,
                            onCareersClick = navController::navigateToCareers,
                            onCabinetClick = navController::navigateToCabinet,
                            onMenuClick = navController::navigateToMenu,
                        )
                    }
                    composable(TopLevelDestination.STATIONS.route) {
                        StationsScreen(onNewsDetailClick = navController::navigateToNewsDetail)
                    }
                    composable(TopLevelDestination.NEWS.route) { NewsScreen(navController::navigateToNewsDetail) }
                    composable(TopLevelDestination.FAVORITES.route) {
                        FavoritesScreen(onNewsDetailClick = navController::navigateToNewsDetail)
                    }

                    stationDetailScreen(navController::navigateToNewsDetail, navController::navigateUp)
                    newsDetailScreen(navController::navigateUp)
                    menuScreen(
                        onContactsClick = navController::navigateToContacts,
                        onFaqClick = navController::navigateToFaq,
                        onCalculatorsClick = navController::navigateToCalculators,
                        onAboutClick = navController::navigateToAbout,
                        onLegalClick = navController::navigateToLegalRegulations,
                        onCareersClick = navController::navigateToCareers,
                        onBackClick = navController::navigateUp,
                    )
                    faqScreen(navController::navigateUp)
                    cabinetScreen(navController::navigateUp)
                    contactsScreen(navController::navigateUp)
                    calculatorsScreen(navController::navigateUp)
                    aboutScreen(navController::navigateUp)
                    legalRegulationsScreen(
                        onTermsAndConditionsPageClick = navController::navigateToTermsAndConditions,
                        onPrivacyPolicyPageClick = navController::navigateToPrivacyPolicy,
                        onLocationInformationPageClick = navController::navigateToLocationInformation,
                        onBackClick = navController::navigateUp,
                    )
                    locationInformationScreen(navController::navigateUp)
                    privacyPolicyScreen(navController::navigateUp)
                    termsAndConditionsScreen(navController::navigateUp)
                    careersScreen(navController::navigateUp)
                    }

                    if (showOnboarding) {
                        NavHost(
                            navController = navController,
                            startDestination = OnboardingNavigationRoute,
                            builder = graphContent,
                        )
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = TopLevelDestination.HOME.route,
                            builder = graphContent,
                        )
                    }
                }
            }
        }
    }
}
