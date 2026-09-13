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
}

kotlin {
    sourceSets {
        // The *what* of a sync (which repositories, fetched in parallel) is shared between
        // platforms via core:data's DataSyncCoordinator/SyncManager contract; this module only
        // holds the platform-specific *how* - WorkManager on Android, BGTaskScheduler on iOS.
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.tracing.ktx)
            implementation(libs.androidx.work.ktx)
            implementation(libs.koin.android)
            implementation(projects.core.analytics)
        }
        androidMain {
            kotlin.srcDir("src/androidMain/kotlin")
            resources.srcDir("src/androidMain/res")
        }
        androidDeviceTest.dependencies {
            implementation(libs.androidx.work.testing)
            implementation(libs.kotlinx.coroutines.guava)
            implementation(projects.core.testing)
            implementation(libs.junit4)
        }
    }
}

dependencies {
    // The `platform()` accessor resolves to the deprecated top-level Kotlin DSL overload (not the
    // dependency-handler-scoped one) inside the sourceSets.androidMain.dependencies {} block above
    // - see core:analytics' build.gradle.kts for the same workaround.
    add("androidMainImplementation", platform(libs.firebase.bom))
}
