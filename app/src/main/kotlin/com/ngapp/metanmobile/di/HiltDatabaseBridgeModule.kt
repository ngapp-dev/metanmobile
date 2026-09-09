package com.ngapp.metanmobile.di

import com.ngapp.metanmobile.core.database.MetanMobileDatabase
import com.ngapp.metanmobile.core.database.dao.career.CareerResourceDao
import com.ngapp.metanmobile.core.database.dao.contact.ContactResourceDao
import com.ngapp.metanmobile.core.database.dao.faq.FaqResourceDao
import com.ngapp.metanmobile.core.database.dao.githubuser.GithubUserResourceDao
import com.ngapp.metanmobile.core.database.dao.location.LocationResourceDao
import com.ngapp.metanmobile.core.database.dao.news.NewsResourceDao
import com.ngapp.metanmobile.core.database.dao.price.PriceResourceDao
import com.ngapp.metanmobile.core.database.dao.station.StationResourceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.mp.KoinPlatform
import javax.inject.Singleton

/**
 * Transitional adapter for the Hilt-owned WorkManager graph.
 * Every binding delegates to the one Koin-managed KMP Room instance.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object HiltDatabaseBridgeModule {
    @Provides
    @Singleton
    fun providesDatabase(): MetanMobileDatabase = KoinPlatform.getKoin().get()

    @Provides fun providesStationDao(database: MetanMobileDatabase): StationResourceDao = database.stationResourceDao()
    @Provides fun providesNewsDao(database: MetanMobileDatabase): NewsResourceDao = database.newsResourceDao()
    @Provides fun providesContactDao(database: MetanMobileDatabase): ContactResourceDao = database.contactResourceDao()
    @Provides fun providesFaqDao(database: MetanMobileDatabase): FaqResourceDao = database.faqResourceDao()
    @Provides fun providesGithubUserDao(database: MetanMobileDatabase): GithubUserResourceDao = database.githubUserResourceDao()
    @Provides fun providesCareerDao(database: MetanMobileDatabase): CareerResourceDao = database.careerResourceDao()
    @Provides fun providesPriceDao(database: MetanMobileDatabase): PriceResourceDao = database.priceResourceDao()
    @Provides fun providesLocationDao(database: MetanMobileDatabase): LocationResourceDao = database.locationResourceDao()
}
