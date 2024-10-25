/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

@file:Suppress("UNCHECKED_CAST")

package com.ngapp.metanmobile.core.datastore.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class CacheStore(context: Context, fileName: String) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = fileName)
    var dataStore = context.dataStore

    suspend fun <T> read(key: String, defaultValue: T): T {
        val preferences = dataStore.data.first()
        return when (defaultValue) {
            is String -> preferences[stringPreferencesKey(key)] as T ?: defaultValue
            is Int -> preferences[intPreferencesKey(key)] as T ?: defaultValue
            is Boolean -> preferences[booleanPreferencesKey(key)] as T ?: defaultValue
            is Long -> preferences[longPreferencesKey(key)] as T ?: defaultValue
            is Double -> preferences[doublePreferencesKey(key)] as T ?: defaultValue
            is Float -> preferences[floatPreferencesKey(key)] as T ?: defaultValue
            else -> defaultValue
        }
    }

    suspend fun <T> write(key: String, value: T) {
        when (value) {
            is String -> dataStore.edit { it[stringPreferencesKey(key)] = value }
            is Int -> dataStore.edit { it[intPreferencesKey(key)] = value }
            is Boolean -> dataStore.edit { it[booleanPreferencesKey(key)] = value }
            is Long -> dataStore.edit { it[longPreferencesKey(key)] = value }
            is Double -> dataStore.edit { it[doublePreferencesKey(key)] = value }
            is Float -> dataStore.edit { it[floatPreferencesKey(key)] = value }
            else -> Unit
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}