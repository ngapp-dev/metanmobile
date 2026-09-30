package com.ngapp.metanmobile.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import com.ngapp.metanmobile.core.datastore.shared.UserPreferences
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.mp.KoinPlatform

actual fun getUserPreferencesDataStore(): DataStore<UserPreferences> {
    val context: Context = KoinPlatform.getKoin().get()
    // Context.dataStoreFile() has always used this directory. Keeping it is what makes an
    // upgrade decode a user's existing preferences instead of silently starting from defaults.
    val producePath = {
        context.filesDir.resolve("datastore").resolve(USER_PREFERENCES_FILE_NAME).absolutePath.toPath()
    }
    return createUserPreferencesDataStore(FileSystem.SYSTEM, producePath)
}
