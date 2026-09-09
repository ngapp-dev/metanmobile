package com.ngapp.metanmobile.core.datastore

import androidx.datastore.core.okio.OkioSerializer
import com.ngapp.metanmobile.core.datastore.shared.UserPreferences
import okio.BufferedSink
import okio.BufferedSource
import okio.IOException

/** Reads the existing protobuf bytes with Wire on both Android and iOS. */
object UserPreferencesSerializer : OkioSerializer<UserPreferences> {
    override val defaultValue = UserPreferences()

    override suspend fun readFrom(source: BufferedSource): UserPreferences =
        try {
            UserPreferences.ADAPTER.decode(source)
        } catch (exception: IOException) {
            throw IllegalStateException("Cannot read user preferences protobuf.", exception)
        }

    override suspend fun writeTo(t: UserPreferences, sink: BufferedSink) {
        sink.write(t.encode())
    }
}
