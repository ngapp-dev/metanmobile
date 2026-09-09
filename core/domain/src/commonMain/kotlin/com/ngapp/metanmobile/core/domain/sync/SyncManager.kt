package com.ngapp.metanmobile.core.domain.sync

import kotlinx.coroutines.flow.Flow

interface SyncManager {
    val isSyncing: Flow<Boolean>
    val syncFailed: Flow<Boolean>
    fun requestSync()
}
