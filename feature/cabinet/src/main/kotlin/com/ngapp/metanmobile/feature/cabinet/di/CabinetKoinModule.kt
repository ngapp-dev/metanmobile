package com.ngapp.metanmobile.feature.cabinet.di

import com.ngapp.metanmobile.feature.cabinet.CabinetViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun cabinetModule(): Module = module {
    viewModel { CabinetViewModel() }
}
