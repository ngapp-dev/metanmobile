package com.ngapp.metanmobile.di

import com.ngapp.metanmobile.MainActivityViewModel
import com.ngapp.metanmobile.core.ui.ads.ConsentHelper
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun mainActivityModule(): Module = module {
    single { ConsentHelper() }
    viewModel { MainActivityViewModel(get(), get()) }
}
