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

package com.ngapp.metanmobile

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.ngapp.metanmobile.core.network.BuildConfig as NetworkBuildConfig
import com.ngapp.metanmobile.core.network.client.di.networkClientModule
import com.ngapp.metanmobile.core.network.di.metanEcogasNetworkModule
import com.ngapp.metanmobile.core.analytics.di.analyticsModule
import com.ngapp.metanmobile.core.data.di.userDataModule
import com.ngapp.metanmobile.core.data.di.locationModule
import com.ngapp.metanmobile.core.database.di.databaseModule
import com.ngapp.metanmobile.core.ui.di.uiModule
import com.ngapp.metanmobile.core.datastore.di.userPreferencesDataStoreModule
import com.ngapp.metanmobile.di.mainActivityModule
import com.ngapp.metanmobile.feature.cabinet.di.cabinetModule
import com.ngapp.metanmobile.feature.careers.di.careersModule
import com.ngapp.metanmobile.feature.contacts.di.contactsModule
import com.ngapp.metanmobile.feature.faq.di.faqModule
import com.ngapp.metanmobile.feature.menu.di.menuModule
import com.ngapp.metanmobile.feature.privacypolicy.di.privacyPolicyModule
import com.ngapp.metanmobile.feature.onboarding.di.onboardingModule
import com.ngapp.metanmobile.feature.news.di.newsModule
import com.ngapp.metanmobile.feature.favorites.di.favoritesModule
import com.ngapp.metanmobile.feature.stations.di.stationsModule
import com.ngapp.metanmobile.feature.home.di.homeModule
import com.ngapp.metanmobile.feature.about.di.aboutModule
import com.ngapp.metanmobile.feature.stationdetail.di.stationDetailModule
import com.ngapp.metanmobile.sync.initializers.Sync
import com.ngapp.metanmobile.sync.di.syncModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.GlobalContext

/**
 * [Application] class for Metan Mobile
 */
@HiltAndroidApp
class MetanMobileApplication : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MetanMobileApplication)
            modules(
                networkClientModule(NetworkBuildConfig.METAN_ECOGAS_API, NetworkBuildConfig.GITHUB_BASE_URL),
                metanEcogasNetworkModule(),
                analyticsModule(),
                databaseModule(),
                uiModule(),
                userPreferencesDataStoreModule(),
                userDataModule(),
                locationModule(),
                syncModule(),
                mainActivityModule(),
                cabinetModule(),
                careersModule(),
                contactsModule(),
                faqModule(),
                menuModule(),
                privacyPolicyModule(),
                onboardingModule(),
                newsModule(),
                favoritesModule(),
                stationsModule(),
                homeModule(),
                aboutModule(),
                stationDetailModule(),
            )
        }
        Sync.initialize(context = this)
    }

    override fun newImageLoader(context: Context): ImageLoader = GlobalContext.get().get()

}
