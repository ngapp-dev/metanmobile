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

package com.ngapp.metanmobile.core.data.test

import com.ngapp.metanmobile.core.data.di.DataModule
import com.ngapp.metanmobile.core.data.repository.career.CareersRepository
import com.ngapp.metanmobile.core.data.repository.contact.ContactsRepository
import com.ngapp.metanmobile.core.data.repository.faq.FaqRepository
import com.ngapp.metanmobile.core.data.repository.githubuser.GithubUserRepository
import com.ngapp.metanmobile.core.data.repository.location.LocationsRepository
import com.ngapp.metanmobile.core.data.repository.news.NewsRepository
import com.ngapp.metanmobile.core.data.repository.news.UserNewsResourceRepository
import com.ngapp.metanmobile.core.data.repository.price.PricesRepository
import com.ngapp.metanmobile.core.data.repository.station.StationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.repository.station.StationsRepository
import com.ngapp.metanmobile.core.data.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.data.test.repository.FakeCareersRepository
import com.ngapp.metanmobile.core.data.test.repository.FakeCompositeStationResourcesWithFavoritesRepository
import com.ngapp.metanmobile.core.data.test.repository.FakeCompositeUserNewsResourceRepository
import com.ngapp.metanmobile.core.data.test.repository.FakeContactsRepository
import com.ngapp.metanmobile.core.data.test.repository.FakeFaqRepository
import com.ngapp.metanmobile.core.data.test.repository.FakeGithubUserRepository
import com.ngapp.metanmobile.core.data.test.repository.FakeLocationsRepository
import com.ngapp.metanmobile.core.data.test.repository.FakeNewsRepository
import com.ngapp.metanmobile.core.data.test.repository.FakePricesRepository
import com.ngapp.metanmobile.core.data.test.repository.FakeStationsRepository
import com.ngapp.metanmobile.core.data.test.repository.FakeUserDataRepository
import com.ngapp.metanmobile.core.data.util.GoogleServicesChecker
import com.ngapp.metanmobile.core.data.util.NetworkMonitor
import com.ngapp.metanmobile.core.data.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class],
)
internal interface TestDataModule {

    @Binds
    fun bindsStationRepository(
        fakeStationsRepository: FakeStationsRepository,
    ): StationsRepository

    @Binds
    fun bindsNewsResourceRepository(
        fakeNewsRepository: FakeNewsRepository,
    ): NewsRepository

    @Binds
    fun bindsLocationsRepository(
        fakeLocationsRepository: FakeLocationsRepository,
    ): LocationsRepository

    @Binds
    fun bindsGithubUserRepository(
        fakeGithubUserRepository: FakeGithubUserRepository,
    ): GithubUserRepository

    @Binds
    fun bindsContactsRepository(
        fakeContactsRepository: FakeContactsRepository,
    ): ContactsRepository

    @Binds
    fun bindsFaqRepository(
        fakeFaqRepository: FakeFaqRepository,
    ): FaqRepository

    @Binds
    fun bindsCareersRepository(
        fakeCareersRepository: FakeCareersRepository,
    ): CareersRepository

    @Binds
    fun bindsPricesRepository(
        fakePricesRepository: FakePricesRepository,
    ): PricesRepository

    @Binds
    fun bindsUserDataRepository(
        userDataRepository: FakeUserDataRepository,
    ): UserDataRepository

    @Binds
    fun bindsStationResourcesWithFavoritesRepository(
        stationResourcesWithFavoritesRepository: FakeCompositeUserNewsResourceRepository,
    ): UserNewsResourceRepository

    @Binds
    fun bindsUserNewsResourceRepository(
        userNewsResourceRepository: FakeCompositeStationResourcesWithFavoritesRepository,
    ): StationResourcesWithFavoritesRepository

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: AlwaysOnlineNetworkMonitor,
    ): NetworkMonitor

    @Binds
    fun bindsGoogleServicesChecker(
        googleServicesAvailabilityChecker: AlwaysHasGoogleServicesAvailabilityChecker,
    ): GoogleServicesChecker

    @Binds
    fun binds(impl: DefaultZoneIdTimeZoneMonitor): TimeZoneMonitor
}
