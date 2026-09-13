package com.ngapp.metanmobile.feature.stationdetail.di
import com.ngapp.metanmobile.feature.stationdetail.StationDetailViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module
val featureStationDetailModule = module { viewModel { StationDetailViewModel(get(), get(), get(), get(), get()) } }
