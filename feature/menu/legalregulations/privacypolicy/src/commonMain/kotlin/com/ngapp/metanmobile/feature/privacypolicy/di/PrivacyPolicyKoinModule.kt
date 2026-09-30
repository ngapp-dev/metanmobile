package com.ngapp.metanmobile.feature.privacypolicy.di

import com.ngapp.metanmobile.feature.privacypolicy.PrivacyPolicyViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featurePrivacyPolicyModule = module {
    viewModel { PrivacyPolicyViewModel(get()) }
}
