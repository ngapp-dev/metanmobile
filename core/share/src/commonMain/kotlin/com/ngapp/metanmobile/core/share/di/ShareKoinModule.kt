package com.ngapp.metanmobile.core.share.di

import com.ngapp.metanmobile.core.share.ShareManager
import org.koin.core.module.Module
import org.koin.dsl.module

fun shareModule(): Module = module {
    single { ShareManager() }
}
