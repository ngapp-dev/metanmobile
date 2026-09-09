package com.ngapp.metanmobile.feature.home.di

import com.ngapp.metanmobile.feature.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun homeModule() = module {
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}
