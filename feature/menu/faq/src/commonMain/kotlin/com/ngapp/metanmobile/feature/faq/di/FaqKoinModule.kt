package com.ngapp.metanmobile.feature.faq.di

import com.ngapp.metanmobile.feature.faq.FaqViewModel
import org.koin.core.module.Module
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureFaqModule: Module = module {
    viewModel { FaqViewModel(get(), get()) }
}
