package com.ngapp.metanmobile.feature.news.di

import com.ngapp.metanmobile.feature.news.detail.NewsDetailViewModel
import com.ngapp.metanmobile.feature.news.list.NewsViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureNewsModule = module {
    viewModel { NewsViewModel(get(), get()) }
    viewModel { NewsDetailViewModel(get(), get(), get()) }
}
