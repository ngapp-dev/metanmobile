package com.ngapp.metanmobile.core.datastore

import androidx.datastore.core.DataStore
import com.ngapp.metanmobile.core.datastore.shared.UserPreferences
import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun getUserPreferencesDataStore(): DataStore<UserPreferences> {
    @OptIn(ExperimentalForeignApi::class)
    val producePath = {
        val directory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )
        requireNotNull(directory).path + "/$USER_PREFERENCES_FILE_NAME"
    }
    return createUserPreferencesDataStore(FileSystem.SYSTEM) { producePath().toPath() }
}
