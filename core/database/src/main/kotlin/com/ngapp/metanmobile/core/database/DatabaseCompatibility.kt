package com.ngapp.metanmobile.core.database

/**
 * Stable persistence identity shared by the legacy Android and KMP Room builders.
 *
 * The database is a pure cache of server feeds plus the sync version describing it
 * ([com.ngapp.metanmobile.core.database.model.syncmeta.SyncMetaEntity]); everything user-owned
 * (favorites, viewed news, settings) lives in DataStore. So a version bump needs no hand-written
 * Migration: both builders fall back to a destructive migration, the sync version is dropped
 * together with the data, and the next sync re-downloads everything. Every released version up
 * to 2.3.1 is on v8 with the same file name and fallback, so they all take that same path.
 * Don't rename the database, though — that would orphan the old file instead of replacing it.
 */
internal const val METAN_MOBILE_DATABASE_NAME = "MetanMobileDb"
internal const val METAN_MOBILE_DATABASE_VERSION = 9
