package com.ngapp.metanmobile.feature.stationdetail.di

import com.ngapp.metanmobile.feature.stationdetail.StationDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun stationDetailModule() = module {
    viewModel { StationDetailViewModel(get(), get(), get(), get(), get()) }
}
