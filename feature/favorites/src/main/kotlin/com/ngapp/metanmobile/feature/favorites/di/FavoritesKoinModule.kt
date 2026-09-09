package com.ngapp.metanmobile.feature.favorites.di

import com.ngapp.metanmobile.feature.favorites.FavoritesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun favoritesModule() = module {
    viewModel { FavoritesViewModel(get(), get(), get()) }
}
