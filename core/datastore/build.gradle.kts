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
    alias(libs.plugins.mm.android.library)
    alias(libs.plugins.mm.android.library.jacoco)
    alias(libs.plugins.mm.hilt)
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "com.ngapp.metanmobile.core.datastore"
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(libs.androidx.dataStore.core)
    api(libs.androidx.dataStore.preferences)
    api(libs.androidx.security)
    api(projects.core.datastoreProto)
    api(projects.core.model)

    implementation(projects.core.common)

    testImplementation(projects.core.datastoreTest)
    testImplementation(libs.kotlinx.coroutines.test)
}
