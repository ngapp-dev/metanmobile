package com.ngapp.metanmobile.feature.menu.di
import com.ngapp.metanmobile.feature.menu.MenuViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module
val featureMenuModule = module { viewModel { MenuViewModel(get()) } }
