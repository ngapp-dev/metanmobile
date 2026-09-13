package com.ngapp.metanmobile.feature.contacts.di

import com.ngapp.metanmobile.feature.contacts.ContactsViewModel
import org.koin.core.module.Module
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureContactsModule: Module = module {
    viewModel { ContactsViewModel(get(), get()) }
}
