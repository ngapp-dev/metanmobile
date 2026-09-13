package com.ngapp.metanmobile.core.data.di

import com.ngapp.metanmobile.core.data.util.ConnectivityManagerNetworkMonitor
import com.ngapp.metanmobile.core.data.util.NetworkMonitor
import com.ngapp.metanmobile.core.data.util.TimeZoneBroadcastMonitor
import com.ngapp.metanmobile.core.data.util.TimeZoneMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-only bindings that have no KMP-shared equivalent yet (network connectivity + timezone
 * broadcast monitoring). Location and station-with-favorites bindings moved to the shared
 * [locationDataModule] once [com.ngapp.metanmobile.core.data.repository.location.PlatformLocationSource]
 * made them cross-platform.
 */
fun locationModule(): Module = module {
    single<NetworkMonitor> { ConnectivityManagerNetworkMonitor(androidContext()) }
    single<TimeZoneMonitor> {
        TimeZoneBroadcastMonitor(
            context = androidContext(),
            appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            ioDispatcher = Dispatchers.IO,
        )
    }
}
