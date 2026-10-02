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

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget

plugins {
    alias(libs.plugins.mm.kmp.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose)
}

kotlin {
    // The widget's provider info (res/xml) is referenced from its manifest, so this module
    // needs real Android resource processing, which KMP library targets have off by default.
    targets.withType<KotlinMultiplatformAndroidLibraryTarget>().configureEach {
        androidResources.enable = true
    }
    sourceSets {
        // "Nearest station" home-screen widget. The Android widget (Glance) lives here; the iOS
        // one is a SwiftUI WidgetKit extension in iosApp that reads the data :widget:core shares.
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:${libs.versions.composePlugin.get()}")
            implementation(projects.core.data)
            implementation(projects.widget.core)
        }
        androidMain.dependencies {
            implementation(projects.resources)
            implementation(projects.core.designsystem)
            implementation(compose.material3)
            implementation(libs.moko.core)
            implementation(libs.moko.compose)
            implementation(libs.koin.android)
        }
        // The Glance layout is tested on the JVM: glance-appwidget-testing renders it without a
        // launcher, Robolectric supplies the Context its strings come from.
        getByName("androidHostTest").dependencies {
            implementation(libs.junit4)
            implementation(libs.robolectric)
            implementation(libs.androidx.test.core)
            implementation(libs.androidx.glance.appwidget.testing)
        }
    }
}
