package com.ngapp.metanmobile.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.ngapp.metanmobile.core.datastore.shared.UserPreferences
import okio.FileSystem
import okio.Path

/**
 * This name and the Android `datastore/` directory are part of the persisted-app contract.
 * Do not change either without an explicit data migration.
 */
internal const val USER_PREFERENCES_FILE_NAME = "user_preferences.pb"

expect fun getUserPreferencesDataStore(): DataStore<UserPreferences>

fun createUserPreferencesDataStore(
    fileSystem: FileSystem,
    producePath: () -> Path,
): DataStore<UserPreferences> =
    DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = fileSystem,
            producePath = producePath,
            serializer = UserPreferencesSerializer,
        ),
    )
