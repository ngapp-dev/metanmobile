package com.ngapp.metanmobile.core.data.di

import com.google.android.gms.location.LocationServices
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.data.repository.location.OfflineFirstLocationsRepository
import com.ngapp.metanmobile.core.data.repository.station.CompositeStationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.util.GoogleServicesAvailabilityChecker
import com.ngapp.metanmobile.core.data.util.GoogleServicesChecker
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

fun locationModule(): Module = module {
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }
    single<GoogleServicesChecker> { GoogleServicesAvailabilityChecker(androidContext()) }
    single<LocationsRepository> { OfflineFirstLocationsRepository(get(), get(), Dispatchers.IO, get()) }
    single<StationResourcesWithFavoritesRepository> {
        CompositeStationResourcesWithFavoritesRepository(get(), get(), get())
    }
}
