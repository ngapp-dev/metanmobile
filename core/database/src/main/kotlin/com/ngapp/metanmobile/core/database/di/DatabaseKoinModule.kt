package com.ngapp.metanmobile.core.database.di

import androidx.room.Room
import com.ngapp.metanmobile.core.database.MetanMobileDatabase
import com.ngapp.metanmobile.core.database.METAN_MOBILE_DATABASE_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/** Android persistence graph. The shared KMP data boundary remains above Room. */
fun databaseModule(): Module = module {
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = MetanMobileDatabase::class.java,
            name = METAN_MOBILE_DATABASE_NAME,
        ).fallbackToDestructiveMigration().build()
    }
    single { get<MetanMobileDatabase>().stationResourceDao() }
    single { get<MetanMobileDatabase>().newsResourceDao() }
    single { get<MetanMobileDatabase>().contactResourceDao() }
    single { get<MetanMobileDatabase>().faqResourceDao() }
    single { get<MetanMobileDatabase>().githubUserResourceDao() }
    single { get<MetanMobileDatabase>().careerResourceDao() }
    single { get<MetanMobileDatabase>().priceResourceDao() }
    single { get<MetanMobileDatabase>().locationResourceDao() }
}
