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
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api("org.jetbrains.compose.runtime:runtime:${libs.versions.composePlugin.get()}")
            api("org.jetbrains.compose.foundation:foundation:${libs.versions.composePlugin.get()}")
            api(compose.material3)
            api(compose.materialIconsExtended)
            api("org.jetbrains.compose.ui:ui:${libs.versions.composePlugin.get()}")
            api("org.jetbrains.compose.ui:ui-util:${libs.versions.composePlugin.get()}")
            implementation(libs.moko.core)
            implementation(libs.moko.compose)
            implementation(libs.coil3.core)
            implementation(libs.coil3.compose)
            implementation(libs.kyant.backdrop)
            api(libs.compose.material3.adaptive)
            api(libs.compose.material3.adaptive.layout)
            api(libs.compose.material3.adaptive.navigation)
            api(libs.compose.material3.adaptive.navigation.suite)
            implementation(projects.resources)
            implementation(projects.core.model)
        }
        // The KMP Android library target's device-test (instrumented-test) compilation is a
        // plain Android-target compilation, not part of the KMP graph — that's why it pulls in
        // the real AndroidX Compose testing artifacts (no org.jetbrains.compose.ui equivalent
        // exists) instead of composePlugin's own version catalog. Pinned to an explicit version
        // rather than the usual androidx-compose-bom platform(): the KMP `dependencies {}` DSL
        // here is `KotlinDependencyHandler`, which has no native `platform()` (unlike Gradle's own
        // `DependencyHandler` that `mm.android.library.compose`'s convention plugin uses), so the
        // only `platform()` in scope is kotlin-dsl's deprecated (error-level) extension function.
        androidDeviceTest.dependencies {
            implementation("androidx.compose.ui:ui-test-junit4:1.11.4")
            implementation("androidx.compose.ui:ui-test-manifest:1.11.4")
            implementation(libs.androidx.activity.compose)
            implementation(libs.junit4)
        }
    }
}

// The JetBrains Compose Resources plugin's asset-copying task doesn't know how to configure
// itself for the KMP Android library target's androidDeviceTest variant (a newer AGP concept than
// the plugin anticipates) and fails validation with an unset outputDirectory — this module doesn't
// use Compose's own resources system at all (everything routes through moko-resources), so there's
// nothing for this task to actually do; disable it rather than working around a misconfiguration
// of a feature that's unused here.
tasks.matching { it.name == "copyAndroidDeviceTestComposeResourcesToAndroidAssets" }
    .configureEach { enabled = false }
