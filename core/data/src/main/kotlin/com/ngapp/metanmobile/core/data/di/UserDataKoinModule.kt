package com.ngapp.metanmobile.core.data.di

import com.ngapp.metanmobile.core.data.repository.user.OfflineFirstUserDataRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.data.repository.githubuser.GithubUserRepository
import com.ngapp.metanmobile.core.data.repository.githubuser.OfflineFirstGithubUserRepository
import com.ngapp.metanmobile.core.data.repository.career.CareersRepository
import com.ngapp.metanmobile.core.data.repository.career.OfflineFirstCareersRepository
import com.ngapp.metanmobile.core.data.repository.contact.ContactsRepository
import com.ngapp.metanmobile.core.data.repository.contact.OfflineFirstContactsRepository
import com.ngapp.metanmobile.core.data.repository.faq.FaqRepository
import com.ngapp.metanmobile.core.data.repository.faq.OfflineFirstFaqRepository
import com.ngapp.metanmobile.core.data.repository.news.NewsRepository
import com.ngapp.metanmobile.core.data.repository.news.OfflineFirstNewsRepository
import com.ngapp.metanmobile.core.data.repository.news.CompositeUserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.news.UserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.price.OfflineFirstPricesRepository
import com.ngapp.metanmobile.core.data.repository.price.PricesRepository
import com.ngapp.metanmobile.core.data.repository.station.OfflineFirstStationsRepository
import com.ngapp.metanmobile.core.data.repository.station.StationsRepository
import org.koin.core.module.Module
import org.koin.dsl.module

fun userDataModule(): Module = module {
    single<UserDataRepository> { OfflineFirstUserDataRepository(get(), get()) }
    single<GithubUserRepository> { OfflineFirstGithubUserRepository(get(), get()) }
    single<StationsRepository> { OfflineFirstStationsRepository(get(), get()) }
    single<NewsRepository> { OfflineFirstNewsRepository(get(), get()) }
    single<UserNewsResourceRepository> { CompositeUserNewsResourceRepository(get(), get()) }
    single<ContactsRepository> { OfflineFirstContactsRepository(get(), get()) }
    single<FaqRepository> { OfflineFirstFaqRepository(get(), get()) }
    single<CareersRepository> { OfflineFirstCareersRepository(get(), get()) }
    single<PricesRepository> { OfflineFirstPricesRepository(get(), get()) }
}
