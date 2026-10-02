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
    // res/drawable holds the shared glass highlight drawables.
    targets.withType<KotlinMultiplatformAndroidLibraryTarget>().configureEach {
        androidResources.enable = true
    }
    sourceSets {
        // Shared plumbing for every home-screen widget: what changes trigger a refresh, and the
        // common look. Each widget lives in its own :widget:* module and only plugs in a
        // WidgetUpdater; widgets read the app's local data and never call the API.
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:${libs.versions.composePlugin.get()}")
            api(projects.core.data)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            api(libs.androidx.glance.appwidget)
            implementation(projects.core.designsystem)
            implementation(projects.resources)
            implementation(compose.material3)
            implementation(libs.moko.core)
            implementation(libs.moko.compose)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
        getByName("androidHostTest").dependencies {
            implementation(libs.junit4)
        }
    }
}
