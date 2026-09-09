package com.ngapp.metanmobile.core.ui.di

import com.ngapp.metanmobile.core.ui.ShareManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun uiModule() = module {
    single { ShareManager(androidContext()) }
}
