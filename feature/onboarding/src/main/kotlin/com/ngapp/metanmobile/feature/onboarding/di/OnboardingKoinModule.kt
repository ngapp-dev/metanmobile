package com.ngapp.metanmobile.feature.onboarding.di

import com.ngapp.metanmobile.feature.onboarding.OnboardingViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun onboardingModule(): Module = module {
    viewModel { OnboardingViewModel(get()) }
}
