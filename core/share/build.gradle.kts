plugins {
    alias(libs.plugins.mm.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            api(projects.core.model)
            implementation(libs.koin.core)
        }
    }
}
