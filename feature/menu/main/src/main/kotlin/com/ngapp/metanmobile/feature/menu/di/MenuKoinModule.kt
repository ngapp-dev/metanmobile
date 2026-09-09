package com.ngapp.metanmobile.feature.menu.di

import com.ngapp.metanmobile.feature.menu.MenuViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun menuModule(): Module = module {
    viewModel { MenuViewModel(get()) }
}
