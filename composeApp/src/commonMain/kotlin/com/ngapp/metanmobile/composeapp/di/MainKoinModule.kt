package com.ngapp.metanmobile.composeapp.di

import com.ngapp.metanmobile.composeapp.MainViewModel
import org.koin.dsl.module

/**
 * Plain `single` (not the `viewModel {}` DSL) — [MainViewModel] is app-lifetime app-init seeding,
 * injected once at the [com.ngapp.metanmobile.composeapp.MetanMobileApp] root via `koinInject()`,
 * the same pattern already used there for `OnboardingViewModel`.
 */
val mainModule = module {
    single { MainViewModel(get()) }
}
