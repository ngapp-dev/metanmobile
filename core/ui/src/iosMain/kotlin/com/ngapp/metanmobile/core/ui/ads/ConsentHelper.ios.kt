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

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Every method here just forwards to whatever Swift registered via registerNativeAdsBridge() -
// see NativeAdsBridge.kt for why the real SDK calls have to live on the Swift side. If nothing
// has been registered yet (shouldn't happen in practice - MetanMobileApp.swift registers it
// before Compose ever mounts), every call is a safe no-op and canShowAds just stays false.
actual class ConsentHelper actual constructor() {
    private val _canShowAds = MutableStateFlow(false)
    actual val canShowAds: StateFlow<Boolean> = _canShowAds.asStateFlow()
    private val bridge get() = NativeAdsRegistry.bridge

    actual fun initializeMobileAdsSdk() {
        bridge?.initializeMobileAdsSdk()
    }

    actual fun isPrivacyOptionsRequired(): Boolean = bridge?.isPrivacyOptionsRequired() ?: false

    actual fun updateConsent() {
        bridge?.updateConsent { canShow -> _canShowAds.value = canShow }
    }

    actual fun obtainConsentAndShow() {
        bridge?.obtainConsentAndShow { canShow -> _canShowAds.value = canShow }
    }

    actual fun revokeConsent() {
        bridge?.revokeConsent()
        _canShowAds.value = false
    }
}
