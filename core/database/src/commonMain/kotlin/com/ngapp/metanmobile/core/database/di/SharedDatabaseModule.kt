package com.ngapp.metanmobile.core.database.di

import com.ngapp.metanmobile.core.database.MetanMobileDatabase
import com.ngapp.metanmobile.core.database.dao.career.CareerResourceDao
import com.ngapp.metanmobile.core.database.dao.contact.ContactResourceDao
import com.ngapp.metanmobile.core.database.dao.faq.FaqResourceDao
import com.ngapp.metanmobile.core.database.dao.githubuser.GithubUserResourceDao
import com.ngapp.metanmobile.core.database.dao.location.LocationResourceDao
import com.ngapp.metanmobile.core.database.dao.news.NewsResourceDao
import com.ngapp.metanmobile.core.database.dao.price.PriceResourceDao
import com.ngapp.metanmobile.core.database.dao.station.StationResourceDao
import com.ngapp.metanmobile.core.database.dao.syncmeta.SyncMetaDao
import com.ngapp.metanmobile.core.database.databaseInstance
import org.koin.core.module.Module
import org.koin.dsl.module

/** Shared persistence graph; database creation itself is supplied by each platform. */
fun databaseModule(): Module = module {
    single<MetanMobileDatabase> { databaseInstance() }
    single<StationResourceDao> { get<MetanMobileDatabase>().stationResourceDao() }
    single<NewsResourceDao> { get<MetanMobileDatabase>().newsResourceDao() }
    single<ContactResourceDao> { get<MetanMobileDatabase>().contactResourceDao() }
    single<FaqResourceDao> { get<MetanMobileDatabase>().faqResourceDao() }
    single<GithubUserResourceDao> { get<MetanMobileDatabase>().githubUserResourceDao() }
    single<CareerResourceDao> { get<MetanMobileDatabase>().careerResourceDao() }
    single<PriceResourceDao> { get<MetanMobileDatabase>().priceResourceDao() }
    single<LocationResourceDao> { get<MetanMobileDatabase>().locationResourceDao() }
    single<SyncMetaDao> { get<MetanMobileDatabase>().syncMetaDao() }
}
