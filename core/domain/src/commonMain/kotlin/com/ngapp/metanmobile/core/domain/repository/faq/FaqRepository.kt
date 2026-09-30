package com.ngapp.metanmobile.core.domain.repository.faq

import com.ngapp.metanmobile.core.domain.sync.Syncable
import com.ngapp.metanmobile.core.model.faq.FaqResource
import kotlinx.coroutines.flow.Flow

data class FaqResourceQuery(val filterFaqListPinned: Boolean = false)

interface FaqRepository : Syncable {
    fun getFaqList(query: FaqResourceQuery = FaqResourceQuery()): Flow<List<FaqResource>>
}
