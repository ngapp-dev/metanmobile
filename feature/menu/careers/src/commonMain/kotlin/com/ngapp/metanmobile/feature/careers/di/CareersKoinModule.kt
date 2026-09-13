package com.ngapp.metanmobile.feature.careers.di

import com.ngapp.metanmobile.feature.careers.CareersViewModel
import org.koin.core.module.Module
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureCareersModule: Module = module {
    viewModel { CareersViewModel(get(), get()) }
}
