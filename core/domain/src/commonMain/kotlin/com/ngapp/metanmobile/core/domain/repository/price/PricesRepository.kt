package com.ngapp.metanmobile.core.domain.repository.price

import com.ngapp.metanmobile.core.domain.sync.Syncable
import com.ngapp.metanmobile.core.model.price.PriceResource
import kotlinx.coroutines.flow.Flow

interface PricesRepository : Syncable {
    fun getFuelPrice(): Flow<PriceResource?>
}
