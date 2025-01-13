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

plugins {
    alias(libs.plugins.mm.android.application)
    alias(libs.plugins.mm.android.application.compose)
    alias(libs.plugins.mm.android.application.jacoco)
    alias(libs.plugins.mm.android.application.firebase)
    alias(libs.plugins.mm.hilt)
    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.roborazzi)
//    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.secrets)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        versionCode =
            libs.versions.versionMajor.get().toInt() * 1000 + libs.versions.versionMinor.get()
                .toInt() * 100 + libs.versions.versionPatch.get().toInt()
        versionName =
            "${libs.versions.versionMajor.get()}.${libs.versions.versionMinor.get()}.${libs.versions.versionPatch.get()}"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = MMBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            applicationIdSuffix = MMBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.named("debug").get()
//            baselineProfile.automaticGenerationDuringBuild = true
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
    implementation(projects.core.analytics)
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.ui)
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

    // Accompanist
    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.google.play.app.update)
    implementation(libs.google.play.app.update.ktx)
    implementation(libs.google.play.app.review)
    implementation(libs.google.play.app.review.ktx)
    implementation(libs.google.play.app.integrity)

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.testManifest)
    debugImplementation(projects.uiTestHiltManifest)

    kspTest(libs.hilt.compiler)

    testImplementation(projects.core.dataTest)
    testImplementation(projects.core.testing)
    testImplementation(libs.hilt.android.testing)

    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(projects.core.screenshotTesting)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.dataTest)
    androidTestImplementation(projects.core.datastoreTest)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.hilt.android.testing)
//    baselineProfile(projects.benchmarks)
}

//baselineProfile {
//    automaticGenerationDuringBuild = false
//    dexLayoutOptimization = true
//}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}
