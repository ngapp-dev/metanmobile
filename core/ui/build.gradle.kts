import java.util.Properties

plugins {
    alias(libs.plugins.mm.kmp.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose)
}

// Reads the real banner ad unit id from the gitignored secrets.properties at the repo root (same
// file :app already reads for MAPS_API_KEY/ADS_ID_KEY) and generates a tiny Kotlin constant from
// it. MainBannerAd.android.kt used to hardcode Google's test unit id directly instead - the KMP
// android library target has no BuildConfig-field mechanism to lean on here (see core:ui's own
// R-class limitation for the same story with resources), so this is the equivalent for a plain
// constant. Falls back to that same Google test id if secrets.properties or the key is missing
// (mirrors app/build.gradle.kts's own getProperty(..., "") fallback), so a fresh checkout without
// secrets.properties still builds and shows real test ads instead of failing.
val generateAdsSecrets = tasks.register("generateAdsSecrets") {
    val secretsFile = rootProject.file("secrets.properties")
    val outputDir = layout.buildDirectory.dir("generated/adsSecrets/kotlin")
    inputs.file(secretsFile).optional(true)
    outputs.dir(outputDir)
    doLast {
        val props = Properties()
        if (secretsFile.exists()) secretsFile.inputStream().use(props::load)
        val adUnitId = props.getProperty("MAIN_BANNER_AD_ID_KEY", "ca-app-pub-3940256099942544/6300978111")
        val yandexAdUnitId = props.getProperty("YANDEX_RU_BANNER_AD_ID_KEY", "demo-banner-yandex")
        // Native "NativeBanner" units; fallbacks are Google's public native test unit and Yandex's
        // native demo unit.
        val nativeAdUnitId = props.getProperty("NATIVE_BANNER_AD_ID_KEY", "ca-app-pub-3940256099942544/2247696110")
        val yandexNativeAdUnitId = props.getProperty("YANDEX_RU_NATIVE_BANNER_AD_ID_KEY", "demo-native-app-yandex")
        val outFile = outputDir.get().file("com/ngapp/metanmobile/core/ui/ads/AdsSecrets.kt").asFile
        outFile.parentFile.mkdirs()
        outFile.writeText(
            "package com.ngapp.metanmobile.core.ui.ads\n\n" +
                "internal const val MAIN_BANNER_AD_UNIT_ID = \"$adUnitId\"\n" +
                "internal const val YANDEX_RU_BANNER_AD_UNIT_ID = \"$yandexAdUnitId\"\n" +
                "internal const val NATIVE_BANNER_AD_UNIT_ID = \"$nativeAdUnitId\"\n" +
                "internal const val YANDEX_RU_NATIVE_BANNER_AD_UNIT_ID = \"$yandexNativeAdUnitId\"\n"
        )
    }
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
            implementation(libs.yandex.mobile.ads)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.browser)
            implementation(libs.google.oss.licenses)
        }
        androidMain {
            kotlin.srcDir("src/androidMain/kotlin")
            kotlin.srcDir(generateAdsSecrets)
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

kotlin {
    sourceSets {
        // Plain JVM tests (ViewModels etc.) - the KMP Android target only picks these up from
        // src/androidHostTest, not the classic src/test.
        getByName("androidHostTest").dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit4)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
