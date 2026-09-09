package com.ngapp.metanmobile.feature.stations.di

import com.ngapp.metanmobile.feature.stations.StationsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun stationsModule() = module {
    viewModel { StationsViewModel(get(), get(), get(), get()) }
}
