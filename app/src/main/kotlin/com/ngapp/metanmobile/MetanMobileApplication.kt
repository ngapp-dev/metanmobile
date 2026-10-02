package com.ngapp.metanmobile

import android.app.Application
import com.ngapp.metanmobile.core.analytics.di.analyticsModule
import com.ngapp.metanmobile.core.data.di.locationDataModule
import com.ngapp.metanmobile.core.data.di.locationModule
import com.ngapp.metanmobile.core.data.di.syncDataModule
import com.ngapp.metanmobile.core.data.di.userDataModule
import com.ngapp.metanmobile.core.data.di.widgetDataModule
import com.ngapp.metanmobile.core.database.di.databaseModule
import com.ngapp.metanmobile.core.datastore.di.userPreferencesDataStoreModule
import com.ngapp.metanmobile.core.network.client.di.networkClientModule
import com.ngapp.metanmobile.core.network.di.metanEcogasNetworkModule
import com.ngapp.metanmobile.core.common.util.UiAndroidPlatformContextProvider as CommonUiAndroidPlatformContextProvider
import com.ngapp.metanmobile.core.ui.UiAndroidPlatformContextProvider
import com.ngapp.metanmobile.core.ui.di.uiModule
import com.ngapp.metanmobile.composeapp.configureImageLoader
import com.ngapp.metanmobile.composeapp.di.mainModule
import com.ngapp.metanmobile.sync.di.syncModule
import com.ngapp.metanmobile.sync.initializers.Sync
import com.ngapp.metanmobile.widget.core.di.widgetCoreModule
import com.ngapp.metanmobile.widget.core.initializers.Widgets
import com.ngapp.metanmobile.widget.nearest.station.di.nearestStationWidgetModule
import com.ngapp.metanmobile.feature.onboarding.di.featureOnboardingModule
import com.ngapp.metanmobile.feature.news.di.featureNewsModule
import com.ngapp.metanmobile.feature.favorites.di.featureFavoritesModule
import com.ngapp.metanmobile.feature.home.di.featureHomeModule
import com.ngapp.metanmobile.feature.stationdetail.di.featureStationDetailModule
import com.ngapp.metanmobile.feature.cabinet.di.featureCabinetModule
import com.ngapp.metanmobile.feature.stations.di.featureStationsModule
import com.ngapp.metanmobile.feature.menu.di.featureMenuModule
import com.ngapp.metanmobile.feature.about.di.featureAboutModule
import com.ngapp.metanmobile.feature.careers.di.featureCareersModule
import com.ngapp.metanmobile.feature.contacts.di.featureContactsModule
import com.ngapp.metanmobile.feature.faq.di.featureFaqModule
import com.ngapp.metanmobile.feature.privacypolicy.di.featurePrivacyPolicyModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/** Android host for the shared KMP dependency graph. */
class MetanMobileApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UiAndroidPlatformContextProvider.setContext(this)
        // core:data can't see core:ui (wrong dependency direction), so it has its own copy of
        // this same provider for Android-only repos like PlatformLocationSource — both need
        // their context set here, or whichever one a given call site resolves to stays null.
        CommonUiAndroidPlatformContextProvider.setContext(this)
        configureImageLoader()
        startKoin {
            androidContext(this@MetanMobileApplication)
            modules(
                networkClientModule(BuildConfig.METAN_ECOGAS_API, BuildConfig.GITHUB_BASE_URL),
                metanEcogasNetworkModule(),
                analyticsModule(),
                databaseModule(),
                userPreferencesDataStoreModule(),
                userDataModule(),
                locationModule(),
                locationDataModule,
                syncDataModule,
                widgetDataModule,
                syncModule(),
                mainModule,
                uiModule,
                featureOnboardingModule,
                featureNewsModule,
                featureFavoritesModule,
                featureHomeModule,
                featureStationDetailModule,
                featureCabinetModule,
                featureStationsModule,
                featureMenuModule,
                featureAboutModule,
                featureCareersModule,
                featureContactsModule,
                featureFaqModule,
                featurePrivacyPolicyModule,
                widgetCoreModule,
                nearestStationWidgetModule,
            )
        }
        Sync.initialize(context = this)
        Widgets.initialize()
    }
}
