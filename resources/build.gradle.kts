plugins {
    alias(libs.plugins.mm.kmp.library)
    alias(libs.plugins.moko.plugin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.moko.core)
            implementation(libs.moko.compose)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("com.ngapp.metanmobile")
    resourcesClassName.set("SharedRes")
}
