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

import app.cash.turbine.test
import com.ngapp.metanmobile.core.data.repository.location.OfflineFirstLocationsRepository
import com.ngapp.metanmobile.core.data.repository.location.PlatformLocationPoint
import com.ngapp.metanmobile.core.data.repository.location.PlatformLocationSource
import com.ngapp.metanmobile.core.data.repository.location.isPlatformLocationAvailable
import com.ngapp.metanmobile.core.database.dao.location.LocationResourceDao
import com.ngapp.metanmobile.core.database.model.location.LocationResourceEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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

// Mirrors the private MAX_LOCATION_FETCH_ATTEMPTS/LOCATION_RETRY_DELAY_MILLIS constants in
// OfflineFirstLocationsRepository.
private const val MAX_ATTEMPTS = 3
private const val RETRY_DELAY_MILLIS = 5_000L

/**
 * Unit tests for the real [OfflineFirstLocationsRepository] implementation, exactly as it stands
 * today - rewritten after the repository moved off `FusedLocationProviderClient`/
 * `GoogleServicesChecker` onto the shared [PlatformLocationSource]/[isPlatformLocationAvailable]
 * abstraction (the previous version of this file mocked a constructor shape and a `getLocationData()`
 * method that no longer exist and had stopped compiling entirely). [LocationResourceDao] is covered
 * with a tiny hand-written fake (project convention); [PlatformLocationSource] and the top-level
 * [isPlatformLocationAvailable] function are covered with MockK, same as before.
 */
class OfflineFirstLocationsRepositoryTest {

    private val dao = FakeLocationResourceDao()
    private val locationSource = mockk<PlatformLocationSource>()

    // Shared with runTest(testDispatcher) below so updateLocation()'s withContext(ioDispatcher)
    // and fetchAndStoreLocationWithRetry()'s delay() run on the same virtual clock - otherwise
    // the retry delays aren't reliably free in test time.
    private val testDispatcher = StandardTestDispatcher()

    private val repository = OfflineFirstLocationsRepository(
        locationResourceDao = dao,
        locationSource = locationSource,
        ioDispatcher = testDispatcher,
    )

    @Before
    fun setUp() {
        mockkStatic(::isPlatformLocationAvailable)
        every { isPlatformLocationAvailable() } returns true
    }

    @After
    fun tearDown() {
        unmockkStatic(::isPlatformLocationAvailable)
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
            coEvery { locationSource.getCurrentLocation() } returns
                PlatformLocationPoint(latitude = 53.0, longitude = 27.0, time = 42L)

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
        coVerify(exactly = 0) { locationSource.getCurrentLocation() }
    }

    @Test
    fun `updateLocation does not fetch when the platform location stack is unavailable`() = runTest(testDispatcher) {
        every { isPlatformLocationAvailable() } returns false

        repository.updateLocation(locationPermissionGranted = true)

        assertEquals(0, dao.upsertCallCount)
        coVerify(exactly = 0) { locationSource.getCurrentLocation() }
    }

    // endregion

    // region updateLocation - fetch/retry

    @Test
    fun `updateLocation stores the location on the first successful attempt`() = runTest(testDispatcher) {
        coEvery { locationSource.getCurrentLocation() } returns
            PlatformLocationPoint(latitude = 53.9, longitude = 27.5, time = 999L)

        repository.updateLocation(locationPermissionGranted = true)

        assertEquals(1, dao.upsertCallCount)
        val stored = repository.getLocationResource().first()
        assertEquals(53.9, stored?.latitude)
        assertEquals(27.5, stored?.longitude)
        coVerify(exactly = 1) { locationSource.getCurrentLocation() }
        // Succeeded on the very first attempt - no retry delay should have been waited at all.
        assertEquals(0L, currentTime)
    }

    @Test
    fun `updateLocation retries and succeeds once a location becomes available`() = runTest(testDispatcher) {
        coEvery { locationSource.getCurrentLocation() } returnsMany listOf(
            null,
            null,
            PlatformLocationPoint(latitude = 1.0, longitude = 2.0, time = 5L),
        )

        repository.updateLocation(locationPermissionGranted = true)

        assertEquals(1, dao.upsertCallCount)
        assertEquals(1.0, repository.getLocationResource().first()?.latitude)
        coVerify(exactly = 3) { locationSource.getCurrentLocation() }
        // 2 failed attempts before the successful 3rd one -> 2 retry delays waited, virtually.
        assertEquals(2 * RETRY_DELAY_MILLIS, currentTime)
    }

    @Test
    fun `updateLocation gives up after the max attempts without crashing`() = runTest(testDispatcher) {
        coEvery { locationSource.getCurrentLocation() } returns null

        repository.updateLocation(locationPermissionGranted = true)

        assertEquals(0, dao.upsertCallCount)
        coVerify(exactly = MAX_ATTEMPTS) { locationSource.getCurrentLocation() }
        assertNull(repository.getLocationResource().first())
        // 3 attempts total -> only 2 gaps between them get a delay, none after the last one.
        assertEquals(2 * RETRY_DELAY_MILLIS, currentTime)
    }

    @Test
    fun `updateLocation propagates an exception thrown while storing the location`() = runTest(testDispatcher) {
        coEvery { locationSource.getCurrentLocation() } returns
            PlatformLocationPoint(latitude = 1.0, longitude = 1.0, time = 1L)
        dao.upsertException = IllegalStateException("Room write failed")

        assertFailsWith<IllegalStateException> {
            repository.updateLocation(locationPermissionGranted = true)
        }
    }

    @Test
    fun `updateLocation replaces the previously stored location, it does not ignore the update`() =
        runTest(testDispatcher) {
            coEvery { locationSource.getCurrentLocation() } returnsMany listOf(
                PlatformLocationPoint(latitude = 10.0, longitude = 10.0, time = 1L),
                PlatformLocationPoint(latitude = 20.0, longitude = 20.0, time = 2L),
            )

            repository.updateLocation(locationPermissionGranted = true)
            repository.updateLocation(locationPermissionGranted = true)

            assertEquals(2, dao.upsertCallCount)
            val all = repository.getLocationResources().first()
            assertEquals(1, all.size)
            assertEquals(20.0, all.first().latitude)
        }

    // endregion

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
