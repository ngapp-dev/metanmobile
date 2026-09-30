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
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation("org.jetbrains.compose.runtime:runtime:${libs.versions.composePlugin.get()}")
        implementation("org.jetbrains.compose.foundation:foundation:${libs.versions.composePlugin.get()}")
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)
        implementation("org.jetbrains.compose.ui:ui:${libs.versions.composePlugin.get()}")
        implementation(libs.navigation.compose)
        implementation(libs.koin.core)
        implementation(libs.koin.compose)
        implementation(libs.koin.compose.viewmodel)
        implementation(libs.moko.compose)
        implementation(libs.lifecycle.viewmodel.kmp)
        implementation(libs.lifecycle.viewmodel.compose.kmp)
        implementation(projects.core.data)
        implementation(projects.core.model)
        implementation(projects.resources)
        implementation(projects.core.designsystem)
        implementation(projects.core.ui)
        implementation(projects.core.analytics)
        implementation(projects.feature.stationdetail)
    }
}

kotlin {
    sourceSets {
        // Plain JVM tests (ViewModels etc.) - the KMP Android target only picks these up from
        // src/androidHostTest, not the classic src/test.
        getByName("androidHostTest").dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit4)
            implementation(libs.kotlinx.coroutines.test)
            implementation(projects.core.testing)
        }
    }
}
