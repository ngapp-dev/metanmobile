package com.ngapp.metanmobile.core.database

import androidx.room.Room
import org.koin.mp.KoinPlatform

actual fun databaseInstance(): MetanMobileDatabase =
    Room.databaseBuilder(
        context = KoinPlatform.getKoin().get(),
        klass = MetanMobileDatabase::class.java,
        name = METAN_MOBILE_DATABASE_NAME,
    ).fallbackToDestructiveMigration().build()
