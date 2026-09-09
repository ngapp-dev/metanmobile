package com.ngapp.metanmobile.core.domain.repository.career

import com.ngapp.metanmobile.core.domain.sync.Syncable
import com.ngapp.metanmobile.core.model.career.CareerResource
import kotlinx.coroutines.flow.Flow

interface CareersRepository : Syncable {
    fun getCareerList(): Flow<List<CareerResource>>
}
