package com.ngapp.metanmobile.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.ngapp.metanmobile.core.datastore.MetanMobilePreferencesDataSource
import com.ngapp.metanmobile.core.datastore.UserPreferences
import com.ngapp.metanmobile.core.datastore.UserPreferencesSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module

fun userPreferencesDataStoreModule(context: Context): Module = module {
    single { UserPreferencesSerializer() }
    single<DataStore<UserPreferences>> {
        DataStoreFactory.create(
            serializer = get(),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        ) { context.dataStoreFile("user_preferences.pb") }
    }
    single { MetanMobilePreferencesDataSource(get()) }
}
