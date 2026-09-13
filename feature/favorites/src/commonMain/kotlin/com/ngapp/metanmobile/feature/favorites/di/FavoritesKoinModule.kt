package com.ngapp.metanmobile.feature.favorites.di

import com.ngapp.metanmobile.feature.favorites.FavoritesViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureFavoritesModule = module {
    viewModel { FavoritesViewModel(get(), get(), get()) }
}
