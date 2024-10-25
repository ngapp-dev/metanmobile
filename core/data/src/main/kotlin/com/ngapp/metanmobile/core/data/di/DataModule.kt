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

package com.ngapp.metanmobile.core.data.di

import com.ngapp.metanmobile.core.data.repository.career.CareersRepository
import com.ngapp.metanmobile.core.data.repository.career.OfflineFirstCareersRepository
import com.ngapp.metanmobile.core.data.repository.contact.ContactsRepository
import com.ngapp.metanmobile.core.data.repository.contact.OfflineFirstContactsRepository
import com.ngapp.metanmobile.core.data.repository.faq.FaqRepository
import com.ngapp.metanmobile.core.data.repository.faq.OfflineFirstFaqRepository
import com.ngapp.metanmobile.core.data.repository.githubuser.GithubUserRepository
import com.ngapp.metanmobile.core.data.repository.githubuser.OfflineFirstGithubUserRepository
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.data.repository.location.OfflineFirstLocationsRepository
import com.ngapp.metanmobile.core.data.repository.news.CompositeUserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.news.NewsRepository
import com.ngapp.metanmobile.core.data.repository.news.OfflineFirstNewsRepository
import com.ngapp.metanmobile.core.data.repository.news.UserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.price.OfflineFirstPricesRepository
import com.ngapp.metanmobile.core.data.repository.price.PricesRepository
import com.ngapp.metanmobile.core.data.repository.station.CompositeStationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.station.OfflineFirstStationsRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.station.StationsRepository
import com.ngapp.metanmobile.core.data.repository.user.OfflineFirstUserDataRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.data.util.ConnectivityManagerNetworkMonitor
import com.ngapp.metanmobile.core.data.util.GoogleServicesAvailabilityChecker
import com.ngapp.metanmobile.core.data.util.GoogleServicesChecker
import com.ngapp.metanmobile.core.data.util.NetworkMonitor
import com.ngapp.metanmobile.core.data.util.TimeZoneBroadcastMonitor
import com.ngapp.metanmobile.core.data.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsStationsRepository(
        stationsRepository: OfflineFirstStationsRepository,
    ): StationsRepository

    @Binds
    internal abstract fun bindsNewsRepository(
        newsRepository: OfflineFirstNewsRepository,
    ): NewsRepository

    @Binds
    internal abstract fun bindsLocationsRepository(
        locationRepository: OfflineFirstLocationsRepository,
    ): LocationsRepository

    @Binds
    internal abstract fun bindsGithubUserRepository(
        githubUserRepository: OfflineFirstGithubUserRepository,
    ): GithubUserRepository

    @Binds
    internal abstract fun bindsContactsRepository(
        contactsRepository: OfflineFirstContactsRepository,
    ): ContactsRepository

    @Binds
    internal abstract fun bindsFaqRepository(
        faqRepository: OfflineFirstFaqRepository,
    ): FaqRepository

    @Binds
    internal abstract fun bindsCareersRepository(
        careersRepository: OfflineFirstCareersRepository,
    ): CareersRepository

    @Binds
    internal abstract fun bindsPricesRepository(
        pricesRepository: OfflineFirstPricesRepository,
    ): PricesRepository

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsStationResourcesWithFavoritesRepository(
        stationResourcesWithFavoritesRepository: CompositeStationResourcesWithFavoritesRepository,
    ): StationResourcesWithFavoritesRepository

    @Binds
    internal abstract fun bindsUserNewsResourceRepository(
        userNewsResourceRepository: CompositeUserNewsResourceRepository,
    ): UserNewsResourceRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun bindsGoogleServicesChecker(
        googleServicesAvailabilityChecker: GoogleServicesAvailabilityChecker,
    ): GoogleServicesChecker

    @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor

}
