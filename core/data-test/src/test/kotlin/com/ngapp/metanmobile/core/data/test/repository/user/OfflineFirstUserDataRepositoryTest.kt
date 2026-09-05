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

package com.ngapp.metanmobile.core.data.test.repository.user

import com.ngapp.metanmobile.core.analytics.AnalyticsEvent
import com.ngapp.metanmobile.core.analytics.AnalyticsHelper
import com.ngapp.metanmobile.core.data.repository.user.OfflineFirstUserDataRepository
import com.ngapp.metanmobile.core.datastore.MetanMobilePreferencesDataSource
import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.UserData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for the real [OfflineFirstUserDataRepository] implementation. It's mostly a thin
 * delegate to [MetanMobilePreferencesDataSource] (mocked - a concrete class, but only 2 of its
 * methods are actually invoked here so a hand-written fake would mean stubbing 9 unused ones),
 * with a handful of methods that also fire an analytics event - those are what's worth pinning
 * down precisely.
 */
class OfflineFirstUserDataRepositoryTest {

    // OfflineFirstUserDataRepository.userData is `val userData: Flow<UserData> =
    // preferencesDataSource.userData`, read once at construction time - the stub must exist
    // before the repository below is built, not inside a @Test method.
    private val userDataFlow = MutableStateFlow(userData())
    private val preferencesDataSource = mockk<MetanMobilePreferencesDataSource>(relaxed = true) {
        every { userData } returns userDataFlow
    }
    private val analyticsHelper = mockk<AnalyticsHelper>(relaxed = true)
    private val repository = OfflineFirstUserDataRepository(preferencesDataSource, analyticsHelper)

    @Test
    fun `userData streams straight through from the preferences data source`() = runTest {
        assertEquals(userData(), repository.userData.first())
    }

    @Test
    fun `setStationResourceFavorite true delegates and logs a saved event`() = runTest {
        repository.setStationResourceFavorite("agnks_1", true)

        coVerify { preferencesDataSource.setStationResourceFavorite("agnks_1", true) }
        val event = slot<AnalyticsEvent>()
        coVerify { analyticsHelper.logEvent(capture(event)) }
        assertEquals("station_resource_saved", event.captured.type)
        assertEquals("agnks_1", event.captured.extras.single().value)
    }

    @Test
    fun `setStationResourceFavorite false delegates and logs an unsaved event`() = runTest {
        repository.setStationResourceFavorite("agnks_1", false)

        coVerify { preferencesDataSource.setStationResourceFavorite("agnks_1", false) }
        val event = slot<AnalyticsEvent>()
        coVerify { analyticsHelper.logEvent(capture(event)) }
        assertEquals("station_resource_unsaved", event.captured.type)
    }

    @Test
    fun `setNewsResourceViewed delegates without logging any analytics event`() = runTest {
        repository.setNewsResourceViewed("news_1", true)

        coVerify { preferencesDataSource.setNewsResourceViewed("news_1", true) }
        coVerify(exactly = 0) { analyticsHelper.logEvent(any()) }
    }

    @Test
    fun `setDarkThemeConfig delegates and logs the new config name`() = runTest {
        repository.setDarkThemeConfig(DarkThemeConfig.DARK)

        coVerify { preferencesDataSource.setDarkThemeConfig(DarkThemeConfig.DARK) }
        val event = slot<AnalyticsEvent>()
        coVerify { analyticsHelper.logEvent(capture(event)) }
        assertEquals("dark_theme_config_changed", event.captured.type)
        assertEquals("DARK", event.captured.extras.single().value)
    }

    @Test
    fun `setShouldHideOnboarding true delegates and logs onboarding_complete`() = runTest {
        repository.setShouldHideOnboarding(true)

        coVerify { preferencesDataSource.setShouldHideOnboarding(true) }
        val event = slot<AnalyticsEvent>()
        coVerify { analyticsHelper.logEvent(capture(event)) }
        assertEquals("onboarding_complete", event.captured.type)
    }

    @Test
    fun `setShouldHideOnboarding false delegates and logs onboarding_reset`() = runTest {
        repository.setShouldHideOnboarding(false)

        coVerify { preferencesDataSource.setShouldHideOnboarding(false) }
        val event = slot<AnalyticsEvent>()
        coVerify { analyticsHelper.logEvent(capture(event)) }
        assertEquals("onboarding_reset", event.captured.type)
    }

    @Test
    fun `setNewsSortingConfig delegates without logging any analytics event`() = runTest {
        val config = NewsSortingConfig.init()

        repository.setNewsSortingConfig(config)

        coVerify { preferencesDataSource.setNewsSortingConfig(config) }
        coVerify(exactly = 0) { analyticsHelper.logEvent(any()) }
    }

    @Test
    fun `setStationSortingConfig delegates without logging any analytics event`() = runTest {
        val config = StationSortingConfig.init()

        repository.setStationSortingConfig(config)

        coVerify { preferencesDataSource.setStationSortingConfig(config) }
        coVerify(exactly = 0) { analyticsHelper.logEvent(any()) }
    }

    @Test
    fun `updateTotalUsageTime delegates without logging any analytics event`() = runTest {
        repository.updateTotalUsageTime(42_000L)

        coVerify { preferencesDataSource.updateTotalUsageTime(42_000L) }
        coVerify(exactly = 0) { analyticsHelper.logEvent(any()) }
    }

    @Test
    fun `setReviewShown delegates without logging any analytics event`() = runTest {
        repository.setReviewShown(true)

        coVerify { preferencesDataSource.setReviewShown(true) }
        coVerify(exactly = 0) { analyticsHelper.logEvent(any()) }
    }

    @Test
    fun `setHomeReorderableList maps enum items to their names before delegating`() = runTest {
        repository.setHomeReorderableList(listOf(HomeContentItem.FAQ, HomeContentItem.CAREER))

        coVerify { preferencesDataSource.setHomeReorderableList(listOf("FAQ", "CAREER")) }
    }

    @Test
    fun `setHomeLastNewsExpanded delegates to the data source's differently-named method`() = runTest {
        repository.setHomeLastNewsExpanded(true)

        coVerify { preferencesDataSource.setHomeExpandedLastNews(true) }
    }

    private fun userData() = UserData(
        favoriteStationResources = emptySet(),
        viewedNewsResources = emptySet(),
        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
        shouldHideOnboarding = false,
        newsSortingConfig = NewsSortingConfig.init(),
        stationSortingConfig = StationSortingConfig.init(),
        totalUsageTime = 0L,
        isReviewShown = false,
        homeReorderableList = emptyList(),
        homeLastNewsExpanded = true,
    )
}
