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
    alias(libs.plugins.mm.kmp.room)
}

kotlin {
    sourceSets {
        commonMain {
            // The existing Room schema, entities and DAOs are platform-neutral. Keeping this
            // source directory during the staged move preserves Android package names and v8 SQL.
            kotlin.srcDir("src/main/kotlin")
            kotlin.exclude("com/ngapp/metanmobile/core/database/di/DaosModule.kt")
            kotlin.exclude("com/ngapp/metanmobile/core/database/di/DatabaseKoinModule.kt")
            kotlin.exclude("com/ngapp/metanmobile/core/database/di/DatabaseModule.kt")
            dependencies {
                api(projects.core.model)
                api(libs.room.runtime)
                implementation(libs.sqlite.bundled)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.koin.core)
            }
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        // Room on the JVM needs an Android Context, hence Robolectric rather than plain commonTest.
        getByName("androidHostTest").dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit4)
            implementation(libs.robolectric)
            implementation(libs.androidx.test.core)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}
