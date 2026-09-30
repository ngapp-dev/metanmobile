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
    alias(libs.plugins.wire)
}

/**
 * The Android proto module is retained only as a compatibility fixture while old releases
 * remain installed. Runtime preferences now use the common Wire schema below, whose field
 * numbers intentionally match the legacy `user_preferences.pb` file.
 */
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.dataStore)
            api(projects.core.model)
            implementation(projects.core.common)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}

wire {
    kotlin {}
    sourcePath {
        srcDir("src/commonMain/proto")
    }
}
