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

package com.ngapp.metanmobile.core.data.test.repository.location

import android.location.Location
import android.util.Log
import app.cash.turbine.test
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.ngapp.metanmobile.core.data.repository.location.OfflineFirstLocationsRepository
import com.ngapp.metanmobile.core.data.util.GoogleServicesChecker
import com.ngapp.metanmobile.core.database.dao.location.LocationResourceDao
import com.ngapp.metanmobile.core.database.model.location.LocationResourceEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

// Mirrors the private LOCATION_RETRY_DELAY_MILLIS constant in OfflineFirstLocationsRepository.
private const val RETRY_DELAY_MILLIS = 5_000L

/**
 * Unit tests for the real [OfflineFirstLocationsRepository] implementation, exactly as it
 * stands today. [LocationResourceDao] and [GoogleServicesChecker] are covered with tiny
 * hand-written fakes (project convention), while the parts that genuinely can't be faked -
 * [FusedLocationProviderClient] and the static [Tasks.await] - are covered with MockK.
 */
class OfflineFirstLocationsRepositoryTest {

    private val dao = FakeLocationResourceDao()
    private val googleServicesChecker = FakeGoogleServicesChecker()
    private val locationClient = mockk<FusedLocationProviderClient>()
    private val lastLocationTask = mockk<Task<Location>>()
    private val currentLocationTask = mockk<Task<Location>>()

    // Shared with runTest(testDispatcher) below so getLocationData()'s withContext(ioDispatcher)
    // and fetchAndStoreLocationWithRetry()'s delay() run on the same virtual clock - otherwise
    // the retry delays aren't reliably free in test time.
    private val testDispatcher = StandardTestDispatcher()

    private val repository = OfflineFirstLocationsRepository(
        locationResourceDao = dao,
        locationClient = locationClient,
        ioDispatcher = testDispatcher,
        googleServicesChecker = googleServicesChecker,
    )

    @Before
    fun setUp() {
        mockkStatic(Tasks::class)
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { locationClient.lastLocation } returns lastLocationTask
        every { locationClient.getCurrentLocation(any<Int>(), any<CancellationToken>()) } returns currentLocationTask
    }

    @After
    fun tearDown() {
        unmockkStatic(Tasks::class)
        unmockkStatic(Log::class)
    }

    // region getLocationResource / getLocationResources

    @Test
    fun `getLocationResource emits null when dao is empty`() = runTest(testDispatcher) {
        assertNull(repository.getLocationResource().first())
    }

    @Test
    fun `getLocationResource emits mapped value when dao has a row`() = runTest(testDispatcher) {
        dao.upsertLocationResources(entity(id = 1, time = 111L, lat = 10.0, lon = 20.0))

        val result = repository.getLocationResource().first()

        assertEquals(10.0, result?.latitude)
        assertEquals(20.0, result?.longitude)
        assertEquals(111L, result?.time)
    }

    @Test
    fun `getLocationResources maps all rows`() = runTest(testDispatcher) {
        dao.upsertLocationResources(entity(id = 1, time = 1L, lat = 1.0, lon = 1.0))
        dao.upsertLocationResources(entity(id = 2, time = 2L, lat = 2.0, lon = 2.0))

        val result = repository.getLocationResources().first()

        assertEquals(2, result.size)
        assertEquals(setOf(1.0, 2.0), result.map { it.latitude }.toSet())
    }

    @Test
    fun `getLocationResource returns the most recent row when there are several`() = runTest(testDispatcher) {
        dao.upsertLocationResources(entity(id = 1, time = 100L, lat = 1.0, lon = 1.0))
        dao.upsertLocationResources(entity(id = 2, time = 300L, lat = 3.0, lon = 3.0))
        dao.upsertLocationResources(entity(id = 3, time = 200L, lat = 2.0, lon = 2.0))

        val result = repository.getLocationResource().first()

        assertEquals(3.0, result?.latitude)
        assertEquals(300L, result?.time)
    }

    @Test
    fun `an active subscriber sees the new location right after updateLocation, without resubscribing`() =
        runTest(testDispatcher) {
            val location = fakeLocation(lat = 53.0, lon = 27.0, time = 42L)
            every { Tasks.await(lastLocationTask) } returns location

            repository.getLocationResource().test {
                assertNull(awaitItem())

                repository.updateLocation(locationPermissionGranted = true)

                val updated = awaitItem()
                assertEquals(53.0, updated?.latitude)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // endregion

    // region updateLocation - gating

    @Test
    fun `updateLocation does nothing when permission is not granted`() = runTest(testDispatcher) {
        repository.updateLocation(locationPermissionGranted = false)

        assertEquals(0, dao.upsertCallCount)
        verify(exactly = 0) { locationClient.lastLocation }
        verify(exactly = 0) { locationClient.getCurrentLocation(any<Int>(), any<CancellationToken>()) }
    }

    @Test
    fun `updateLocation does not store anything when Google Services are unavailable`() = runTest(testDispatcher) {
        googleServicesChecker.available = false

        repository.updateLocation(locationPermissionGranted = true)

        assertEquals(0, dao.upsertCallCount)
        verify(exactly = 0) { locationClient.lastLocation }
        verify(exactly = 0) { locationClient.getCurrentLocation(any<Int>(), any<CancellationToken>()) }
    }

    @Test
    fun `updateLocation swallows an exception from the availability check`() = runTest(testDispatcher) {
        googleServicesChecker.error = IllegalStateException("boom")

        // Must not throw.
        repository.updateLocation(locationPermissionGranted = true)

        assertEquals(0, dao.upsertCallCount)
    }

    // endregion

    // region updateLocation - fetch/retry

    @Test
    fun `updateLocation stores the location on the first successful attempt`() = runTest(testDispatcher) {
        val location = fakeLocation(lat = 53.9, lon = 27.5, time = 999L)
        every { Tasks.await(lastLocationTask) } returns location

        repository.updateLocation(locationPermissionGranted = true)

        assertEquals(1, dao.upsertCallCount)
        val stored = repository.getLocationResource().first()
        assertEquals(53.9, stored?.latitude)
        assertEquals(27.5, stored?.longitude)
        verify(exactly = 0) { locationClient.getCurrentLocation(any<Int>(), any<CancellationToken>()) }
        // Succeeded on the very first attempt - no retry delay should have been waited at all.
        assertEquals(0L, currentTime)
    }

    @Test
    fun `updateLocation retries and succeeds once a location becomes available`() = runTest(testDispatcher) {
        val location = fakeLocation(lat = 1.0, lon = 2.0, time = 5L)
        every { Tasks.await(lastLocationTask) } returns null
        every { Tasks.await(currentLocationTask) } returnsMany listOf(null, null, location)

        repository.updateLocation(locationPermissionGranted = true)

        assertEquals(1, dao.upsertCallCount)
        assertEquals(1.0, repository.getLocationResource().first()?.latitude)
        verify(exactly = 3) { Tasks.await(currentLocationTask) }
        // 2 failed attempts before the successful 3rd one -> 2 retry delays waited, virtually.
        assertEquals(2 * RETRY_DELAY_MILLIS, currentTime)
    }

    @Test
    fun `updateLocation gives up after the max attempts without crashing`() = runTest(testDispatcher) {
        every { Tasks.await(lastLocationTask) } returns null
        every { Tasks.await(currentLocationTask) } returns null

        repository.updateLocation(locationPermissionGranted = true)

        assertEquals(0, dao.upsertCallCount)
        verify(exactly = 3) { Tasks.await(currentLocationTask) }
        assertNull(repository.getLocationResource().first())
        // 3 attempts total -> only 2 gaps between them get a delay, none after the last one.
        assertEquals(2 * RETRY_DELAY_MILLIS, currentTime)
    }

    @Test
    fun `updateLocation propagates an exception thrown while storing the location`() = runTest(testDispatcher) {
        val location = fakeLocation(lat = 1.0, lon = 1.0, time = 1L)
        every { Tasks.await(lastLocationTask) } returns location
        dao.upsertException = IllegalStateException("Room write failed")

        // Documents current behavior, not a requirement: the availability check is the only
        // thing wrapped in runCatching in updateLocation() - a failure while actually storing
        // the location is not caught and propagates to the caller.
        assertFailsWith<IllegalStateException> {
            repository.updateLocation(locationPermissionGranted = true)
        }
    }

    @Test
    fun `updateLocation replaces the previously stored location, it does not ignore the update`() =
        runTest(testDispatcher) {
            val first = fakeLocation(lat = 10.0, lon = 10.0, time = 1L)
            val second = fakeLocation(lat = 20.0, lon = 20.0, time = 2L)
            every { Tasks.await(lastLocationTask) } returnsMany listOf(first, second)

            repository.updateLocation(locationPermissionGranted = true)
            repository.updateLocation(locationPermissionGranted = true)

            assertEquals(2, dao.upsertCallCount)
            val all = repository.getLocationResources().first()
            assertEquals(1, all.size)
            assertEquals(20.0, all.first().latitude)
        }

    // endregion

    // region getLocationData

    @Test
    fun `getLocationData returns the cached last location without an active request`() = runTest(testDispatcher) {
        val location = fakeLocation(lat = 3.0, lon = 4.0, time = 7L)
        every { Tasks.await(lastLocationTask) } returns location

        val result = repository.getLocationData()

        assertEquals(location, result)
        verify(exactly = 0) { locationClient.getCurrentLocation(any<Int>(), any<CancellationToken>()) }
    }

    @Test
    fun `getLocationData falls back to an active request when there is no cached location`() =
        runTest(testDispatcher) {
            val location = fakeLocation(lat = 3.0, lon = 4.0, time = 7L)
            every { Tasks.await(lastLocationTask) } returns null
            every { Tasks.await(currentLocationTask) } returns location

            val result = repository.getLocationData()

            assertEquals(location, result)
            verify(exactly = 1) { locationClient.getCurrentLocation(any<Int>(), any<CancellationToken>()) }
        }

    @Test
    fun `getLocationData returns null when both the cache and the active request fail`() =
        runTest(testDispatcher) {
            every { Tasks.await(lastLocationTask) } throws RuntimeException("no cache")
            every { Tasks.await(currentLocationTask) } throws RuntimeException("no fix")

            assertNull(repository.getLocationData())
        }

    // endregion

    private fun fakeLocation(lat: Double, lon: Double, time: Long): Location {
        val location = mockk<Location>()
        every { location.latitude } returns lat
        every { location.longitude } returns lon
        every { location.time } returns time
        return location
    }

    private fun entity(id: Int, time: Long, lat: Double, lon: Double) = LocationResourceEntity(
        id = id,
        time = time,
        latitude = lat,
        longitude = lon,
    )
}

/** Tiny hand-written fake, in keeping with the project's existing testing convention. */
private class FakeLocationResourceDao : LocationResourceDao {

    private val state = MutableStateFlow<List<LocationResourceEntity>>(emptyList())

    var upsertCallCount = 0
        private set

    var upsertException: Throwable? = null

    override suspend fun upsertLocationResources(locationResourceEntity: LocationResourceEntity) {
        upsertException?.let { throw it }
        upsertCallCount++
        state.value = (state.value.filterNot { it.id == locationResourceEntity.id } + locationResourceEntity)
            .sortedByDescending { it.time }
    }

    override suspend fun deleteLocationResources() {
        state.value = emptyList()
    }

    override fun getLocationResources(): Flow<List<LocationResourceEntity>> = state
}

/** Tiny hand-written fake, in keeping with the project's existing testing convention. */
private class FakeGoogleServicesChecker : GoogleServicesChecker {
    var available: Boolean = true
    var error: Throwable? = null

    override val isGoogleServicesAvailable: Boolean
        get() = error?.let { throw it } ?: available
}
