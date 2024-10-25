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

package com.ngapp.metanmobile

import android.app.Application
import androidx.startup.AppInitializer
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.ngapp.metanmobile.sync.initializers.Sync
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * [Application] class for Metan Mobile
 */
@HiltAndroidApp
class MetanMobileApplication : Application(), ImageLoaderFactory {

    @Inject
    lateinit var imageLoader: dagger.Lazy<ImageLoader>

    override fun onCreate() {
        super.onCreate()
        Sync.initialize(context = this)
    }

    override fun newImageLoader(): ImageLoader = imageLoader.get()

}