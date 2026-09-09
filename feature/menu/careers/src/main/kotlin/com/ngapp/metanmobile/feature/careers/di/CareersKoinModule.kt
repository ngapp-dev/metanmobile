package com.ngapp.metanmobile.feature.careers.di

import com.ngapp.metanmobile.feature.careers.CareersViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun careersModule(): Module = module {
    viewModel { CareersViewModel(get(), get()) }
}
