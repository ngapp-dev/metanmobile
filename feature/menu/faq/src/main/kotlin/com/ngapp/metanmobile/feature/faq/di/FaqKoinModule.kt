package com.ngapp.metanmobile.feature.faq.di

import com.ngapp.metanmobile.feature.faq.FaqViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun faqModule(): Module = module {
    viewModel { FaqViewModel(get(), get()) }
}
