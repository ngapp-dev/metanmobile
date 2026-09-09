package com.ngapp.metanmobile.feature.news.di

import com.ngapp.metanmobile.feature.news.list.NewsViewModel
import com.ngapp.metanmobile.feature.news.detail.NewsDetailViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun newsModule(): Module = module {
    viewModel { NewsViewModel(get(), get(), get()) }
    viewModel { NewsDetailViewModel(get(), get(), get(), get()) }
}
