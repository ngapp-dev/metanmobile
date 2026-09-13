package com.ngapp.metanmobile.sync.di

import com.ngapp.metanmobile.core.data.util.SyncManager
import com.ngapp.metanmobile.sync.status.WorkManagerSyncManager
import com.ngapp.metanmobile.sync.workers.SyncWorker
import android.content.Context
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

fun syncModule(): Module = module {
    single<SyncManager> { WorkManagerSyncManager(androidContext()) }
    factory<SyncWorker> { params ->
        SyncWorker(
            appContext = params.get<Context>(),
            workerParams = params.get<WorkerParameters>(),
            dataSyncCoordinator = get(),
            ioDispatcher = Dispatchers.IO,
            analyticsHelper = get(),
        )
    }
}
