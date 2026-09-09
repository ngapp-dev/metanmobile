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

package com.ngapp.metanmobile

import com.ngapp.metanmobile.core.testing.repository.TestUserDataRepository
import com.ngapp.metanmobile.core.testing.repository.emptyUserData
import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.core.ui.ads.ConsentHelper
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Consent must be requested exactly once, right when onboarding is dismissed, and never again for
 * unrelated user data changes afterward - see [MainActivityViewModel.onObserveConsent].
 *
 * Collecting the whole [com.ngapp.metanmobile.core.model.userdata.UserData] flow instead of just
 * `shouldHideOnboarding` used to re-run [ConsentHelper.obtainConsentAndShow] on every emission
 * (usage-time ticks every 30s, sorting config, home list reorders, ...), which could re-enter
 * [ConsentHelper] while a consent form was still loading and leave ads disabled - see
 * [ConsentHelper.showingForm].
 */
class MainActivityViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val consentHelper = mockk<ConsentHelper>()
    private val canShowAds = MutableStateFlow(false)

    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setup() {
        every { consentHelper.obtainConsentAndShow() } just Runs
        every { consentHelper.canShowAds } returns canShowAds

        viewModel = MainActivityViewModel(userDataRepository, consentHelper)
    }

    @Test
    fun `consent is not requested before onboarding is dismissed`() = runTest {
        userDataRepository.setUserData(emptyUserData.copy(shouldHideOnboarding = false))

        verify(exactly = 0) { consentHelper.obtainConsentAndShow() }
    }

    @Test
    fun `consent is requested exactly once when onboarding is dismissed`() = runTest {
        userDataRepository.setUserData(emptyUserData.copy(shouldHideOnboarding = true))

        verify(exactly = 1) { consentHelper.obtainConsentAndShow() }
    }

    @Test
    fun `consent is requested only once even as unrelated user data keeps changing afterward`() =
        runTest {
            userDataRepository.setUserData(emptyUserData.copy(shouldHideOnboarding = true))
            verify(exactly = 1) { consentHelper.obtainConsentAndShow() }

            // None of these touch onboarding - they used to re-trigger the whole consent flow
            // regardless, because onObserveConsent collected every emission of the raw UserData
            // flow instead of just shouldHideOnboarding.
            userDataRepository.setReviewShown(true)
            userDataRepository.updateTotalUsageTime(30_000)
            userDataRepository.setHomeLastNewsExpanded(false)

            verify(exactly = 1) { consentHelper.obtainConsentAndShow() }
        }

    @Test
    fun `consent is requested again if onboarding is dismissed, undone, then dismissed again`() =
        runTest {
            userDataRepository.setUserData(emptyUserData.copy(shouldHideOnboarding = true))
            userDataRepository.setUserData(emptyUserData.copy(shouldHideOnboarding = false))
            userDataRepository.setUserData(emptyUserData.copy(shouldHideOnboarding = true))

            verify(exactly = 2) { consentHelper.obtainConsentAndShow() }
        }

    @Test
    fun `consentState canShowAds mirrors the consent helper`() = runTest {
        assertEquals(false, viewModel.consentState.value.canShowAds)

        canShowAds.value = true

        assertEquals(true, viewModel.consentState.value.canShowAds)

        canShowAds.value = false

        assertEquals(false, viewModel.consentState.value.canShowAds)
    }
}
