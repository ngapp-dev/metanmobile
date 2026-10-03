plugins {
    alias(libs.plugins.mm.kmp.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose)
    // Needed on whichever module actually produces the iOS .framework (this one, not
    // :resources) so moko-resources wires its "copy resources into the app" task into
    // embedAndSignAppleFrameworkForXcode — see the SharedRes bundle-not-found runtime crash.
    alias(libs.plugins.moko.plugin)
}

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "MetanMobileComposeApp"
            isStatic = true
            // Without this, moko-resources can't find its bundle at runtime on iOS
            // ("bundle with identifier ... not found") — the umbrella framework needs to
            // re-export :resources so its compiled resource bundle actually ships inside
            // MetanMobileComposeApp.framework instead of staying an internal-only dependency.
            export(projects.resources)
            // core:ui's NativeAdsBridge interface (and registerNativeAdsBridge()) needs to reach
            // the framework's generated Objective-C header so MobileAdsBridge.swift can actually
            // implement it - same "export needs an api dependency" requirement as above.
            export(projects.core.ui)
            // widget:core's WidgetReloader is implemented in Swift (WidgetCenter is Swift-only),
            // so it has to be visible in the framework header too.
            export(projects.widget.core)
            // AnalyticsBridge is implemented in Swift (FirebaseAnalyticsBridge in MetanMobileApp.swift).
            export(projects.core.analytics)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:${libs.versions.composePlugin.get()}")
            implementation("org.jetbrains.compose.foundation:foundation:${libs.versions.composePlugin.get()}")
            implementation(compose.materialIconsExtended)
            implementation(compose.material3)
            implementation("org.jetbrains.compose.ui:ui:${libs.versions.composePlugin.get()}")
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.lifecycle.viewmodel.kmp)
            implementation(libs.navigation.compose)
            implementation(libs.moko.core)
            implementation(libs.moko.compose)
            implementation(libs.coil3.core)
            implementation(libs.coil3.network.ktor)
            implementation(libs.coil3.svg)
            implementation(projects.core.model)
            implementation(projects.core.data)
            implementation(projects.core.datastore)
            implementation(projects.core.analytics)
            implementation(projects.core.designsystem)
            api(projects.core.ui)
            // api, not implementation: binaries.framework { export(projects.resources) } above
            // requires the exported dependency to also be an API dependency of this source set.
            api(projects.resources)
            implementation(projects.feature.onboarding)
            implementation(projects.feature.home)
            implementation(projects.feature.stations)
            implementation(projects.feature.news)
            implementation(projects.feature.favorites)
            implementation(projects.feature.stationdetail)
            implementation(projects.feature.cabinet)
            implementation(projects.feature.menu.main)
            implementation(projects.feature.menu.about)
            implementation(projects.feature.menu.calculators)
            implementation(projects.feature.menu.careers)
            implementation(projects.feature.menu.contacts)
            implementation(projects.feature.menu.faq)
            implementation(projects.feature.menu.legalregulations.main)
            implementation(projects.feature.menu.legalregulations.locationinformation)
            implementation(projects.feature.menu.legalregulations.privacypolicy)
            implementation(projects.feature.menu.legalregulations.termsandconditions)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        // sync:work holds the platform-specific sync mechanism (WorkManager on Android,
        // BGTaskScheduler on iOS) - SharedKoin.kt's initSharedKoin() wires up the iOS side
        // (iosSyncModule/registerBackgroundSync) from here, same as MetanMobileApplication does
        // for Android's own WorkManager wiring via the :app module's own dependency on it.
        iosMain.dependencies {
            implementation(projects.sync.work)
            api(projects.widget.core)
            api(projects.core.analytics)
            implementation(projects.widget.nearestStation)
        }
    }
}
