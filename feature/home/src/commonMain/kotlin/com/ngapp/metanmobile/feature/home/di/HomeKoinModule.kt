package com.ngapp.metanmobile.feature.home.di

import com.ngapp.metanmobile.feature.home.HomeViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureHomeModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}
