package com.ngapp.metanmobile.core.database

/** Creates the platform-specific Room database instance used by the shared Koin graph. */
expect fun databaseInstance(): MetanMobileDatabase
