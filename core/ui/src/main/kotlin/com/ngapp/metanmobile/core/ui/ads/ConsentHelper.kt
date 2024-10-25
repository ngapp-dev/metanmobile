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
 */

package com.ngapp.metanmobile.core.ui.ads

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.ngapp.metanmobile.core.common.util.UiAndroidPlatformContextProvider
import com.ngapp.metanmobile.core.common.util.UiAndroidPlatformContextProvider.getActivity
import com.ngapp.metanmobile.core.ui.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

class ConsentHelper {
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)
    private var showingForm = false
    private val _canShowAds = MutableStateFlow(false)
    val canShowAds: StateFlow<Boolean> = _canShowAds.asStateFlow()
    private val context = requireNotNull(UiAndroidPlatformContextProvider.context?.getActivity())

    @SuppressLint("MissingPermission")
    fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        MobileAds.initialize(context)
    }

    fun isPrivacyOptionsRequired(): Boolean {
        val ci = UserMessagingPlatform.getConsentInformation(context)
        return ci.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    fun updateConsent() {
        UserMessagingPlatform.showPrivacyOptionsForm(context) { error ->
            val ci = UserMessagingPlatform.getConsentInformation(context)
            handleConsentResult(ci)
        }
    }

    fun obtainConsentAndShow() {
        val params = if (BuildConfig.DEBUG) {
            val debugSettings = ConsentDebugSettings.Builder(context)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId(BuildConfig.ADS_TEST_DEVICE_ID)
                .build()
            ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .setConsentDebugSettings(debugSettings)
                .build()
        } else {
            ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build()
        }

        val ci = UserMessagingPlatform.getConsentInformation(context)
        ci.requestConsentInfoUpdate(context, params, {
            if (showingForm) return@requestConsentInfoUpdate
            showingForm = true
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(context) { error ->
                showingForm = false
                handleConsentResult(ci)
            }
        }, { error ->
            Log.w("ConsentHelper", "${error.errorCode}: ${error.message}")
        })

        if (ci.canRequestAds()) {
            initializeMobileAdsSdk()
            _canShowAds.value = true
        }
    }

    fun revokeConsent() {
        val ci = UserMessagingPlatform.getConsentInformation(context)
        ci.reset()
        _canShowAds.value = false
    }

    private fun handleConsentResult(ci: ConsentInformation) {
        if (ci.canRequestAds()) {
            initializeMobileAdsSdk()
            _canShowAds.value = true
        } else {
            _canShowAds.value = false
        }
    }
}
