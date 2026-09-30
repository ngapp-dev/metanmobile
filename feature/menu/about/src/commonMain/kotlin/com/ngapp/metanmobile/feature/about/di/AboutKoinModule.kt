package com.ngapp.metanmobile.feature.about.di

import com.ngapp.metanmobile.feature.about.AboutViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureAboutModule = module {
    viewModel { AboutViewModel(get(), get()) }
}
