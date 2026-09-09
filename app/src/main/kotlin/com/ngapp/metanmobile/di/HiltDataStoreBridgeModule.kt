package com.ngapp.metanmobile.di

import com.ngapp.metanmobile.core.datastore.MetanMobilePreferencesDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.mp.KoinPlatform
import javax.inject.Singleton

/**
 * Keeps the remaining Hilt-only entry points on the same KMP DataStore instance as Koin while
 * the Android graph is being retired incrementally.
 */
@Module
@InstallIn(SingletonComponent::class)
object HiltDataStoreBridgeModule {
    @Provides
    @Singleton
    fun providesPreferencesDataSource(): MetanMobilePreferencesDataSource =
        KoinPlatform.getKoin().get()
}
