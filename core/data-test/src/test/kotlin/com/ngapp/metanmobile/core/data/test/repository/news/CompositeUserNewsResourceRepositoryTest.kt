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

package com.ngapp.metanmobile.core.data.test.repository.news

import app.cash.turbine.test
import com.ngapp.metanmobile.core.data.Synchronizer
import com.ngapp.metanmobile.core.data.repository.news.CompositeUserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.news.NewsRepository
import com.ngapp.metanmobile.core.data.repository.news.NewsResourceQuery
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.model.userdata.DarkThemeConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingType
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.UserData
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the real [CompositeUserNewsResourceRepository] implementation - the news
 * counterpart of
 * [com.ngapp.metanmobile.core.data.test.repository.station.CompositeStationResourcesWithFavoritesRepositoryTest],
 * minus the location/distance/favorites-filtering logic that repository also has.
 */
class CompositeUserNewsResourceRepositoryTest {

    private val newsRepository = FakeNewsRepository()
    private val userDataFlow = MutableStateFlow(userData())
    private val userDataRepository = mockk<UserDataRepository> {
        every { userData } returns userDataFlow
    }

    private val repository = CompositeUserNewsResourceRepository(newsRepository, userDataRepository)

    @Test
    fun `observeAll calls getNewsResourcesDesc when the user's sorting order is DESC`() = runTest {
        userDataFlow.value = userData(sortingOrder = SortingOrder.DESC)
        newsRepository.emit(listOf(news(id = "1")))

        repository.observeAll(NewsResourceQuery()).first()

        assertEquals(1, newsRepository.descQueries.size)
        assertTrue(newsRepository.ascQueries.isEmpty())
    }

    @Test
    fun `observeAll calls getNewsResourcesAsc when the user's sorting order is ASC`() = runTest {
        userDataFlow.value = userData(sortingOrder = SortingOrder.ASC)
        newsRepository.emit(listOf(news(id = "1")))

        repository.observeAll(NewsResourceQuery()).first()

        assertEquals(1, newsRepository.ascQueries.size)
        assertTrue(newsRepository.descQueries.isEmpty())
    }

    @Test
    fun `observeAll overrides the query's sortingType with the user's configured one`() = runTest {
        userDataFlow.value = userData(sortingOrder = SortingOrder.DESC, sortingType = NewsSortingType.NAME)
        newsRepository.emit(emptyList())

        repository.observeAll(NewsResourceQuery()).first()

        assertEquals(NewsSortingType.NAME, newsRepository.descQueries.single().sortingType)
    }

    @Test
    fun `observeAll marks news present in the user's viewed set as hasBeenViewed`() = runTest {
        userDataFlow.value = userData(viewed = setOf("viewed_id"))
        newsRepository.emit(listOf(news(id = "viewed_id"), news(id = "unviewed_id")))

        val result = repository.observeAll(NewsResourceQuery()).first()

        assertEquals(setOf("viewed_id"), result.filter { it.hasBeenViewed }.map { it.id }.toSet())
    }

    @Test
    fun `an active subscriber sees results switch queries when the user's sorting order changes`() = runTest {
        userDataFlow.value = userData(sortingOrder = SortingOrder.DESC)
        newsRepository.emit(listOf(news(id = "1")))

        repository.observeAll(NewsResourceQuery()).test {
            awaitItem()
            assertEquals(1, newsRepository.descQueries.size)

            userDataFlow.value = userData(sortingOrder = SortingOrder.ASC)

            awaitItem()
            assertEquals(1, newsRepository.ascQueries.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun news(id: String) = NewsResource(
        id = id,
        code = id,
        isPinned = 0,
        previewPicture = "",
        detailPicture = "",
        isActive = 1,
        isOperate = 1,
        relatedStation = "",
        title = id,
        dateCreated = Clock.System.now().toEpochMilliseconds(),
        description = "",
        content = "",
        url = "",
        isSearchable = 1,
    )

    private fun userData(
        sortingOrder: SortingOrder = SortingOrder.DESC,
        sortingType: NewsSortingType = NewsSortingType.DATE,
        viewed: Set<String> = emptySet(),
    ) = UserData(
        favoriteStationResources = emptySet(),
        viewedNewsResources = viewed,
        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
        shouldHideOnboarding = false,
        newsSortingConfig = NewsSortingConfig(sortingType = sortingType, sortingOrder = sortingOrder),
        stationSortingConfig = StationSortingConfig.init(),
        totalUsageTime = 0L,
        isReviewShown = false,
        homeReorderableList = emptyList(),
        homeLastNewsExpanded = true,
    )
}

/** Tiny hand-written fake, in keeping with the project's existing testing convention. */
private class FakeNewsRepository : NewsRepository {

    private val state = MutableStateFlow<List<NewsResource>>(emptyList())

    val ascQueries = mutableListOf<NewsResourceQuery>()
    val descQueries = mutableListOf<NewsResourceQuery>()

    fun emit(newsResources: List<NewsResource>) {
        state.value = newsResources
    }

    override fun getNewsResourcesAsc(query: NewsResourceQuery): Flow<List<NewsResource>> {
        ascQueries += query
        return state
    }

    override fun getNewsResourcesDesc(query: NewsResourceQuery): Flow<List<NewsResource>> {
        descQueries += query
        return state
    }

    override fun getNewsResource(newsId: String): Flow<NewsResource> =
        throw NotImplementedError("not used by CompositeUserNewsResourceRepository")

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
