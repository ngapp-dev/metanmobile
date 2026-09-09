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

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.ngapp.metanmobile.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/** Base convention for shared Android and iOS library modules. */
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.multiplatform")
            apply(plugin = "com.android.kotlin.multiplatform.library")

            extensions.configure<KotlinMultiplatformExtension> {
                targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
                    namespace = "com.ngapp.metanmobile" + path.replace(':', '.')
                    compileSdk = libs.findVersion("androidCompileSdk").get().toString().toInt()
                    minSdk = libs.findVersion("androidMinSdk").get().toString().toInt()
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_11)
                    }
                }

                iosArm64()
                iosSimulatorArm64()

                sourceSets.commonTest.dependencies {
                    implementation(libs.findLibrary("kotlin.test").get())
                }
            }
        }
    }
}
