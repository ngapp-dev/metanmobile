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

plugins {
    alias(libs.plugins.mm.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir("src/main/kotlin")
            kotlin.exclude("com/ngapp/metanmobile/core/data/di/LocationKoinModule.kt")
            kotlin.exclude("com/ngapp/metanmobile/core/data/util/ConnectivityManagerNetworkMonitor.kt")
            kotlin.exclude("com/ngapp/metanmobile/core/data/repository/location/PlatformLocationSource.android.kt")
            kotlin.exclude("com/ngapp/metanmobile/core/data/util/TimeZoneMonitor.kt")
            dependencies {
                api(projects.core.common)
                api(projects.core.domain)
                api(projects.core.database)
                api(projects.core.datastore)
                api(projects.core.network)
                implementation(projects.core.analytics)
                implementation(libs.koin.core)
            }
        }
        androidMain {
            kotlin.srcDir("src/main/kotlin")
            kotlin.include("com/ngapp/metanmobile/core/data/di/LocationKoinModule.kt")
            kotlin.include("com/ngapp/metanmobile/core/data/util/ConnectivityManagerNetworkMonitor.kt")
            kotlin.include("com/ngapp/metanmobile/core/data/repository/location/PlatformLocationSource.android.kt")
            kotlin.include("com/ngapp/metanmobile/core/data/util/TimeZoneMonitor.kt")
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.play.services.location)
                implementation(libs.androidx.localbroadcastmanager)
                implementation(libs.androidx.tracing.ktx)
            }
        }
    }
}
