package com.ngapp.metanmobile.feature.privacypolicy.di

import com.ngapp.metanmobile.feature.privacypolicy.PrivacyPolicyViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun privacyPolicyModule(): Module = module {
    viewModel { PrivacyPolicyViewModel(get()) }
}
