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

import com.ngapp.metanmobile.MMBuildType
import java.util.Properties

plugins {
    alias(libs.plugins.mm.android.application)
    alias(libs.plugins.mm.android.application.compose)
    alias(libs.plugins.mm.android.application.jacoco)
    alias(libs.plugins.mm.android.application.firebase)
    alias(libs.plugins.google.osslicenses)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.secrets)
    alias(libs.plugins.kotlin.serialization)
}

android {
    // Network endpoints are supplied by the Secrets Gradle plugin and consumed when
    // the shared Ktor client is registered in the Android application graph.
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        versionCode =
            libs.versions.versionMajor.get().toInt() * 1000 + libs.versions.versionMinor.get()
                .toInt() * 100 + libs.versions.versionPatch.get().toInt()
        versionName =
            "${libs.versions.versionMajor.get()}.${libs.versions.versionMinor.get()}.${libs.versions.versionPatch.get()}"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resourceConfigurations += listOf("en", "ru", "be")

        // The secrets plugin (applied below) only injects secrets.properties into each real
        // variant's manifestPlaceholders via the new Variant API - the unitTest component that
        // testOptions.unitTests.isIncludeAndroidResources merges the manifest for has no such
        // provider to receive it, so mergeDebugUnitTestManifest fails to resolve
        // ${MAPS_API_KEY}/${ADS_ID_KEY}. Mirroring the two placeholders the manifest actually
        // uses into this classic map (which that merge does read) fixes it for unit tests too.
        val secretsProperties = Properties().apply {
            rootProject.file("secrets.properties").takeIf { it.exists() }
                ?.inputStream()?.use(::load)
        }
        manifestPlaceholders["MAPS_API_KEY"] = secretsProperties.getProperty("MAPS_API_KEY", "")
        manifestPlaceholders["ADS_ID_KEY"] = secretsProperties.getProperty("ADS_ID_KEY", "")
    }

    buildTypes {
    debug {
            applicationIdSuffix = MMBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            applicationIdSuffix = MMBuildType.RELEASE.applicationIdSuffix
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.named("debug").get()
        }
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "com.ngapp.metanmobile"
}

secrets {
    defaultPropertiesFileName = "secrets.properties"
}

dependencies {
    implementation(projects.composeApp)
    // Firebase Performance still uses lite generated protobuf messages at runtime. This was
    // previously brought in transitively by the Android-only DataStore proto module.
    implementation(libs.protobuf.kotlin.lite)

    implementation(projects.core.analytics)
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.ui)
    implementation(projects.core.network)
    implementation(projects.core.networkClient)
    implementation(projects.sync.work)

    implementation(projects.feature.cabinet)
    implementation(projects.feature.favorites)
    implementation(projects.feature.home)
    implementation(projects.feature.menu.about)
    implementation(projects.feature.menu.calculators)
    implementation(projects.feature.menu.careers)
    implementation(projects.feature.menu.contacts)
    implementation(projects.feature.menu.faq)
    implementation(projects.feature.menu.legalregulations.locationinformation)
    implementation(projects.feature.menu.legalregulations.main)
    implementation(projects.feature.menu.legalregulations.privacypolicy)
    implementation(projects.feature.menu.legalregulations.termsandconditions)
    implementation(projects.feature.menu.main)
    implementation(projects.feature.news)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.stations)
    implementation(projects.feature.stationdetail)

    // Accompanist
    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.coil3.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.android)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.google.play.app.update)
    implementation(libs.google.play.app.update.ktx)
    implementation(libs.google.play.app.review)
    implementation(libs.google.play.app.review.ktx)
    implementation(libs.google.play.app.integrity)

    debugImplementation(libs.androidx.compose.ui.testManifest)

    testImplementation(projects.core.dataTest)
    testImplementation(projects.core.datastore)
    testImplementation(projects.core.datastoreProto)
    testImplementation(projects.core.testing)

    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.mockk)
    testImplementation(projects.core.screenshotTesting)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.dataTest)
    androidTestImplementation(projects.core.datastoreTest)
    androidTestImplementation(projects.composeApp)
    androidTestImplementation(projects.resources)
    androidTestImplementation(libs.moko.core)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.navigation.testing)
//    baselineProfile(projects.benchmarks)
}

//baselineProfile {
//    automaticGenerationDuringBuild = false
//    dexLayoutOptimization = true
//}

dependencyGuard {
    configuration("releaseRuntimeClasspath")
}
