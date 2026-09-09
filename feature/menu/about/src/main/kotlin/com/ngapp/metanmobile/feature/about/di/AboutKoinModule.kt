package com.ngapp.metanmobile.feature.about.di

import com.ngapp.metanmobile.feature.about.AboutViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun aboutModule() = module {
    viewModel { AboutViewModel(get(), get()) }
}
