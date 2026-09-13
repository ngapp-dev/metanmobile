package com.ngapp.metanmobile.feature.cabinet.di
import com.ngapp.metanmobile.feature.cabinet.CabinetViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module
val featureCabinetModule = module { viewModel { CabinetViewModel() } }
