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

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "metanmobile"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":benchmarks")
include(":core:analytics")
include(":core:common")
include(":core:data")
include(":core:data-test")
include(":core:database")
include(":core:datastore")
include(":core:datastore-proto")
include(":core:datastore-test")
include(":core:designsystem")
include(":core:model")
include(":core:network")
include(":core:screenshot-testing")
include(":core:testing")
include(":core:ui")

include(":feature:cabinet")
include(":feature:favorites")
include(":feature:home")
include(":feature:menu:about")
include(":feature:menu:calculators")
include(":feature:menu:careers")
include(":feature:menu:contacts")
include(":feature:menu:faq")
include(":feature:menu:legalregulations:locationinformation")
include(":feature:menu:legalregulations:main")
include(":feature:menu:legalregulations:privacypolicy")
include(":feature:menu:legalregulations:termsandconditions")
include(":feature:menu:main")
include(":feature:news")
include(":feature:onboarding")
include(":feature:stations")
include(":feature:stationdetail")

include(":lint")
include(":sync:work")
include(":sync:sync-test")
include(":ui-test-hilt-manifest")
