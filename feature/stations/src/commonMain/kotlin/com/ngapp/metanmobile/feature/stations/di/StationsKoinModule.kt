package com.ngapp.metanmobile.feature.stations.di

import com.ngapp.metanmobile.feature.stations.StationsViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureStationsModule = module { viewModel { StationsViewModel(get(), get(), get(), get()) } }
