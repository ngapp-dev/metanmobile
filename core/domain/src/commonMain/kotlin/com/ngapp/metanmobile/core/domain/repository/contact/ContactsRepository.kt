package com.ngapp.metanmobile.core.domain.repository.contact

import com.ngapp.metanmobile.core.domain.sync.Syncable
import com.ngapp.metanmobile.core.model.contact.ContactResource
import kotlinx.coroutines.flow.Flow

interface ContactsRepository : Syncable {
    fun getContactResource(): Flow<ContactResource?>
}
