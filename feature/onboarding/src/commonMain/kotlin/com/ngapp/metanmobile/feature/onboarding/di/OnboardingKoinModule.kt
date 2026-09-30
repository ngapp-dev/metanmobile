package com.ngapp.metanmobile.feature.onboarding.di

import com.ngapp.metanmobile.feature.onboarding.OnboardingViewModel
import org.koin.core.module.Module
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureOnboardingModule: Module = module {
    viewModel { OnboardingViewModel(get()) }
}
