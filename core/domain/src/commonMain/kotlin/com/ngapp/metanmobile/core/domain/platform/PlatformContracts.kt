package com.ngapp.metanmobile.core.domain.platform

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}
