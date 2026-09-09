package com.ngapp.metanmobile.core.domain.repository.news

import com.ngapp.metanmobile.core.domain.sync.Syncable
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.model.news.UserNewsResource
import com.ngapp.metanmobile.core.model.userdata.NewsSortingType
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import kotlinx.coroutines.flow.Flow

data class NewsResourceQuery(
    val filterNewsIds: Set<String>? = null,
    val filterNewsPinned: Boolean = false,
    val filterNewsByStationTitle: String? = null,
    val sortingType: NewsSortingType = NewsSortingType.DATE,
    val sortingOrder: SortingOrder = SortingOrder.DESC,
    val searchQuery: String = "",
)

interface NewsRepository : Syncable {
    fun getNewsResourcesAsc(query: NewsResourceQuery = NewsResourceQuery()): Flow<List<NewsResource>>
    fun getNewsResourcesDesc(query: NewsResourceQuery = NewsResourceQuery()): Flow<List<NewsResource>>
    fun getNewsResource(newsId: String): Flow<NewsResource>
}

interface UserNewsResourceRepository {
    fun observeAll(query: NewsResourceQuery): Flow<List<UserNewsResource>>
}
