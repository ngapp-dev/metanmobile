package com.ngapp.metanmobile.core.datastore.di

import androidx.datastore.core.DataStore
import com.ngapp.metanmobile.core.datastore.MetanMobilePreferencesDataSource
import com.ngapp.metanmobile.core.datastore.getUserPreferencesDataStore
import com.ngapp.metanmobile.core.datastore.shared.UserPreferences
import org.koin.core.module.Module
import org.koin.dsl.module

fun userPreferencesDataStoreModule(): Module = module {
    single<DataStore<UserPreferences>> { getUserPreferencesDataStore() }
    single { MetanMobilePreferencesDataSource(get()) }
}
