package com.ngapp.metanmobile.core.analytics.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.ngapp.metanmobile.core.analytics.AnalyticsHelper
import com.ngapp.metanmobile.core.analytics.FirebaseAnalyticsHelper
import org.koin.core.module.Module
import org.koin.dsl.module

fun analyticsModule(): Module = module {
    single { Firebase.analytics }
    single<AnalyticsHelper> { FirebaseAnalyticsHelper(get<FirebaseAnalytics>()) }
}
