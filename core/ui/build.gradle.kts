plugins {
    alias(libs.plugins.mm.kmp.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.analytics)
            api(projects.core.common)
            api(projects.core.designsystem)
            api(projects.core.model)
            implementation(projects.resources)
            implementation(libs.moko.compose)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(libs.navigation.compose)
            implementation(libs.coil3.core)
            implementation(libs.coil3.compose)
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.metrics)
            implementation(libs.androidx.activity.compose)
            implementation(libs.accompanist.permissions)
            implementation(libs.androidx.appcompat)
            implementation(libs.google.services.ads)
            implementation(libs.google.services.base)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.browser)
            implementation(libs.google.oss.licenses)
        }
        androidMain {
            kotlin.srcDir("src/androidMain/kotlin")
            resources.srcDir("src/androidMain/res")
        }
        // See core:designsystem's androidDeviceTest block for why this is pinned to an explicit
        // version instead of the usual androidx-compose-bom platform().
        androidDeviceTest.dependencies {
            implementation("androidx.compose.ui:ui-test-junit4:1.11.4")
            implementation("androidx.compose.ui:ui-test-manifest:1.11.4")
            implementation(libs.androidx.activity.compose)
            implementation(libs.junit4)
        }
    }
}

// See core:designsystem's build.gradle.kts for why this is disabled.
tasks.matching { it.name == "copyAndroidDeviceTestComposeResourcesToAndroidAssets" }
    .configureEach { enabled = false }
