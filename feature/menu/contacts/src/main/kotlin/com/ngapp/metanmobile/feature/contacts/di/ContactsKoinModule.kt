package com.ngapp.metanmobile.feature.contacts.di

import com.ngapp.metanmobile.feature.contacts.ContactsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun contactsModule(): Module = module {
    viewModel { ContactsViewModel(get(), get()) }
}
