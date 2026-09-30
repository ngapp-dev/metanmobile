package com.ngapp.metanmobile.core.domain.sync

import kotlin.coroutines.cancellation.CancellationException

interface Synchronizer {
    suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)
}

interface Syncable {
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

suspend fun <T> Synchronizer.updateDataSync(
    dataFetcher: suspend () -> List<T>,
    dataWriter: suspend (List<T>) -> Unit,
): Boolean = runSync(dataFetcher, dataWriter)

suspend fun <T> Synchronizer.updateSingleDataSync(
    dataFetcher: suspend () -> T,
    dataWriter: suspend (T) -> Unit,
): Boolean = runSync(dataFetcher, dataWriter)

private suspend fun <T> runSync(
    fetch: suspend () -> T,
    write: suspend (T) -> Unit,
): Boolean = try {
    write(fetch())
    true
} catch (exception: CancellationException) {
    throw exception
} catch (exception: Exception) {
    // A sync failure here (network error, JSON shape mismatch, DB write error, ...) used to be
    // completely silent - the caller just sees an empty/stale list with no clue why. Printing it
    // at least surfaces the real exception in Logcat/Xcode console instead of a guessing game.
    println("Sync failed: ${exception::class.simpleName}: ${exception.message}")
    false
}
