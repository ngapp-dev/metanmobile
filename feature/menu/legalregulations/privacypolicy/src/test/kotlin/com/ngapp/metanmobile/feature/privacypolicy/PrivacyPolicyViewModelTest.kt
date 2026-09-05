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

package com.ngapp.metanmobile.feature.privacypolicy

import com.ngapp.metanmobile.core.testing.util.MainDispatcherRule
import com.ngapp.metanmobile.core.ui.ads.ConsentHelper
import com.ngapp.metanmobile.feature.privacypolicy.state.PrivacyPolicyAction
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PrivacyPolicyViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val consentHelper = mockk<ConsentHelper>()

    @Test
    fun `isPrivacyOptionsRequired is initially false, before the consent helper has been read`() =
        runTest {
            every { consentHelper.isPrivacyOptionsRequired() } returns true
            val viewModel = PrivacyPolicyViewModel(consentHelper)

            assertEquals(false, viewModel.isPrivacyOptionsRequired.value)
        }

    @Test
    fun `isPrivacyOptionsRequired reflects the consent helper once collected`() = runTest {
        every { consentHelper.isPrivacyOptionsRequired() } returns true
        val viewModel = PrivacyPolicyViewModel(consentHelper)

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.isPrivacyOptionsRequired.collect()
        }

        assertEquals(true, viewModel.isPrivacyOptionsRequired.value)
    }

    @Test
    fun `isPrivacyOptionsRequired is false when the consent helper says so`() = runTest {
        every { consentHelper.isPrivacyOptionsRequired() } returns false
        val viewModel = PrivacyPolicyViewModel(consentHelper)

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.isPrivacyOptionsRequired.collect()
        }

        assertEquals(false, viewModel.isPrivacyOptionsRequired.value)
    }

    @Test
    fun `UpdateConsent action updates consent when privacy options are required`() = runTest {
        every { consentHelper.isPrivacyOptionsRequired() } returns true
        every { consentHelper.updateConsent() } returns Unit
        val viewModel = PrivacyPolicyViewModel(consentHelper)
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.isPrivacyOptionsRequired.collect()
        }

        viewModel.triggerAction(PrivacyPolicyAction.UpdateConsent)

        verify(exactly = 1) { consentHelper.updateConsent() }
    }

    @Test
    fun `UpdateConsent action does nothing when privacy options are not required`() = runTest {
        every { consentHelper.isPrivacyOptionsRequired() } returns false
        val viewModel = PrivacyPolicyViewModel(consentHelper)
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.isPrivacyOptionsRequired.collect()
        }

        viewModel.triggerAction(PrivacyPolicyAction.UpdateConsent)

        verify(exactly = 0) { consentHelper.updateConsent() }
    }
}
