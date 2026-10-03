/*
 * Copyright 2026 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.core.analytics

/**
 * An analytics SDK reached from outside Kotlin. iOS implements it in Swift
 * (FirebaseAnalyticsBridge in MetanMobileApp.swift): the Firebase iOS SDK has no Kotlin API.
 */
fun interface AnalyticsBridge {
    fun logEvent(name: String, parameters: Map<String, String>)
}

/**
 * [AnalyticsHelper] that forwards events to an [AnalyticsBridge], truncating parameter keys and
 * values to Firebase's maximum lengths, as Android's FirebaseAnalyticsHelper does.
 */
class BridgedAnalyticsHelper(private val bridge: AnalyticsBridge) : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        bridge.logEvent(
            name = event.type,
            parameters = event.extras.associate { it.key.take(MAX_KEY_LENGTH) to it.value.take(MAX_VALUE_LENGTH) },
        )
    }

    private companion object {
        const val MAX_KEY_LENGTH = 40
        const val MAX_VALUE_LENGTH = 100
    }
}
