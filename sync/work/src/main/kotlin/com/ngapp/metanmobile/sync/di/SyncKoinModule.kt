package com.ngapp.metanmobile.sync.di

import com.ngapp.metanmobile.core.data.util.SyncManager
import com.ngapp.metanmobile.sync.status.WorkManagerSyncManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

fun syncModule(): Module = module {
    single<SyncManager> { WorkManagerSyncManager(androidContext()) }
}
