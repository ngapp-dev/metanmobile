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

package com.ngapp.metanmobile.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ngapp.metanmobile.core.database.dao.career.CareerResourceDao
import com.ngapp.metanmobile.core.database.dao.contact.ContactResourceDao
import com.ngapp.metanmobile.core.database.dao.faq.FaqResourceDao
import com.ngapp.metanmobile.core.database.dao.githubuser.GithubUserResourceDao
import com.ngapp.metanmobile.core.database.dao.location.LocationResourceDao
import com.ngapp.metanmobile.core.database.dao.news.NewsResourceDao
import com.ngapp.metanmobile.core.database.dao.price.PriceResourceDao
import com.ngapp.metanmobile.core.database.dao.station.StationResourceDao
import com.ngapp.metanmobile.core.database.model.career.CareerResourceEntity
import com.ngapp.metanmobile.core.database.model.contact.ContactResourceEntity
import com.ngapp.metanmobile.core.database.model.faq.FaqResourceEntity
import com.ngapp.metanmobile.core.database.model.githubuser.GithubUserResourceEntity
import com.ngapp.metanmobile.core.database.model.location.LocationResourceEntity
import com.ngapp.metanmobile.core.database.model.news.NewsResourceEntity
import com.ngapp.metanmobile.core.database.model.price.PriceResourceEntity
import com.ngapp.metanmobile.core.database.model.station.StationResourceEntity
import com.ngapp.metanmobile.core.database.util.InstantConverter
import com.ngapp.metanmobile.core.database.util.ListStringConverter

@Database(
    entities = [
        StationResourceEntity::class,
        NewsResourceEntity::class,
        ContactResourceEntity::class,
        FaqResourceEntity::class,
        GithubUserResourceEntity::class,
        CareerResourceEntity::class,
        PriceResourceEntity::class,
        LocationResourceEntity::class,
    ],
    version = 5,
    exportSchema = false,
)
@TypeConverters(
    InstantConverter::class,
    ListStringConverter::class,
)
abstract class MetanMobileDatabase : RoomDatabase() {
    abstract fun stationResourceDao(): StationResourceDao
    abstract fun newsResourceDao(): NewsResourceDao
    abstract fun contactResourceDao(): ContactResourceDao
    abstract fun faqResourceDao(): FaqResourceDao
    abstract fun githubUserResourceDao(): GithubUserResourceDao
    abstract fun careerResourceDao(): CareerResourceDao
    abstract fun priceResourceDao(): PriceResourceDao
    abstract fun locationResourceDao(): LocationResourceDao
}