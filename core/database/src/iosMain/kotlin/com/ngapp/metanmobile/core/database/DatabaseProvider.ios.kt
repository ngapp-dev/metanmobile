package com.ngapp.metanmobile.core.database

import androidx.room.Room
import androidx.room.util.findDatabaseConstructorAndInitDatabaseImpl
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
private fun documentsDirectory(): String {
    val directory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(directory).path!!
}

actual fun databaseInstance(): MetanMobileDatabase =
    Room.databaseBuilder<MetanMobileDatabase>(
        name = "${documentsDirectory()}/$METAN_MOBILE_DATABASE_NAME",
        factory = { findDatabaseConstructorAndInitDatabaseImpl(MetanMobileDatabase::class) },
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
