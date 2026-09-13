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

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.dependencyGuard) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.spotless) apply true
    alias(libs.plugins.wire) apply false
}

// The `org.jetbrains.compose` Gradle plugin (composePlugin = 1.11.1) pins its own default
// material3 sub-artifact to 1.9.0 — several minor releases behind the rest of the Compose
// Multiplatform suite (compose.ui/foundation/animation already resolve to 1.11.1 project-wide,
// see the same dependency tree). Material3 Expressive's wavy progress indicators
// (LinearWavyProgressIndicator/CircularWavyProgressIndicator, master's loading-state look) only
// became a *public* API — as opposed to existing internally, unusable outside the module — in
// 1.10.0+; forcing every module's material3 resolution up to 1.11.0-alpha07 (already the version
// actually downloaded/cached for this project, so this adds no new artifact) is what actually
// unlocks them. Master itself shipped on an alpha material3 (androidx 1.5.0-alpha04) for the same
// reason, so this isn't a new category of risk versus what's being restored.
subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.compose.material3" && requested.name == "material3") {
                useVersion("1.11.0-alpha07")
                because("unlock Material3 Expressive's wavy progress indicators, matching master")
            }
        }
    }
}
