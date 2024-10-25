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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun providesStationResourceDao(database: MetanMobileDatabase): StationResourceDao =
        database.stationResourceDao()

    @Provides
    fun providesNewsResourceDao(database: MetanMobileDatabase): NewsResourceDao =
        database.newsResourceDao()

    @Provides
    fun providesContactResourceDao(database: MetanMobileDatabase): ContactResourceDao =
        database.contactResourceDao()

    @Provides
    fun providesFaqResourceDao(database: MetanMobileDatabase): FaqResourceDao =
        database.faqResourceDao()

    @Provides
    fun providesGithubUserResourceDao(database: MetanMobileDatabase): GithubUserResourceDao =
        database.githubUserResourceDao()

    @Provides
    fun providesCareerResourceDao(database: MetanMobileDatabase): CareerResourceDao =
        database.careerResourceDao()

    @Provides
    fun providesPriceResourceDao(database: MetanMobileDatabase): PriceResourceDao =
        database.priceResourceDao()

    @Provides
    fun providesLocationResourceDao(database: MetanMobileDatabase): LocationResourceDao =
        database.locationResourceDao()
}
