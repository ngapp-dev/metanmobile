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

package com.ngapp.metanmobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
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
import com.ngapp.metanmobile.feature.favorites.navigation.favoritesScreen
import com.ngapp.metanmobile.feature.home.navigation.homeScreen
import com.ngapp.metanmobile.feature.legalregulations.navigation.legalRegulationsScreen
import com.ngapp.metanmobile.feature.legalregulations.navigation.navigateToLegalRegulations
import com.ngapp.metanmobile.feature.menu.navigation.menuScreen
import com.ngapp.metanmobile.feature.menu.navigation.navigateToMenu
import com.ngapp.metanmobile.feature.news.detail.navigation.navigateToNewsDetail
import com.ngapp.metanmobile.feature.news.detail.navigation.newsDetailScreen
import com.ngapp.metanmobile.feature.news.list.navigation.newsScreen
import com.ngapp.metanmobile.feature.onboarding.navigation.onboardingScreen
import com.ngapp.metanmobile.feature.privacypolicy.navigation.navigateToPrivacyPolicy
import com.ngapp.metanmobile.feature.privacypolicy.navigation.privacyPolicyScreen
import com.ngapp.metanmobile.feature.settings.ui.legalregulations.locationinformation.navigation.locationInformationScreen
import com.ngapp.metanmobile.feature.settings.ui.legalregulations.locationinformation.navigation.navigateToLocationInformation
import com.ngapp.metanmobile.feature.stations.navigation.stationsScreen
import com.ngapp.metanmobile.feature.termsandconditions.navigation.navigateToTermsAndConditions
import com.ngapp.metanmobile.feature.termsandconditions.navigation.termsAndConditionsScreen
import com.ngapp.metanmobile.ui.MMAppState
import kotlin.reflect.KClass

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun MMNavHost(
    appState: MMAppState,
    onShowBottomBar: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    startDestination: KClass<*>,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        onboardingScreen(onSkipOnboarding = appState::navigateFromOnboardingToHomeScreen)
        homeScreen(
            onNewsClick = appState::navigateToNewsListFromHomeScreen,
            onNewsDetailClick = navController::navigateToNewsDetail,
            onFaqListClick = navController::navigateToFaq,
            onCareersClick = navController::navigateToCareers,
            onCabinetClick = navController::navigateToCabinet,
            onSettingsClick = navController::navigateToMenu,
            onShowBottomBar = onShowBottomBar,
        )
        stationsScreen(
            onNewsDetailClick = navController::navigateToNewsDetail,
            onShowBottomBar = onShowBottomBar,
        )
        newsScreen(onNewsDetailClick = navController::navigateToNewsDetail)
        favoritesScreen(
            onNewsDetailClick = navController::navigateToNewsDetail,
            onShowBottomBar = onShowBottomBar,
        )
        newsDetailScreen(onBackClick = navController::navigateUp)
        menuScreen(
            onContactsPageClick = navController::navigateToContacts,
            onFaqPageClick = navController::navigateToFaq,
            onCalculatorsPageClick = navController::navigateToCalculators,
            onAboutPageClick = navController::navigateToAbout,
            onLegalRegulationsPageClick = navController::navigateToLegalRegulations,
            onCareerPageClick = navController::navigateToCareers,
            onRefreshPage = appState::refreshSettingsPage,
            onBackClick = navController::navigateUp,
        )
        faqScreen(onBackClick = navController::navigateUp)
        cabinetScreen(onBackClick = navController::navigateUp)
        contactsScreen(onBackClick = navController::navigateUp)
        calculatorsScreen(onBackClick = navController::navigateUp)
        aboutScreen(onBackClick = navController::navigateUp)
        legalRegulationsScreen(
            onTermsAndConditionsPageClick = navController::navigateToTermsAndConditions,
            onPrivacyPolicyPageClick = navController::navigateToPrivacyPolicy,
            onLocationInformationPageClick = navController::navigateToLocationInformation,
            onBackClick = navController::navigateUp
        )
        locationInformationScreen(onBackClick = navController::navigateUp)
        privacyPolicyScreen(onBackClick = navController::navigateUp)
        termsAndConditionsScreen(onBackClick = navController::navigateUp)
        careersScreen(onBackClick = navController::navigateUp)
    }
}