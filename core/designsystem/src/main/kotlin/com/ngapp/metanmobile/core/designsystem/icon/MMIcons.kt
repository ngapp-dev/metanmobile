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

package com.ngapp.metanmobile.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationDisabled
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.ngapp.metanmobile.core.designsystem.R

/**
 * Metan Mobile icons. Material icons are [ImageVector]s, custom icons are drawable resource IDs.
 */
object MMIcons {

    // Bottom bar icons
    val HomeFilled = Icons.Filled.Home
    val HomeOutlined = Icons.Outlined.Home
    val StationsFilled = Icons.Filled.Map
    val StationsOutlined = Icons.Outlined.Map
    val NewsFilled = Icons.AutoMirrored.Filled.LibraryBooks
    val NewsOutlined = Icons.AutoMirrored.Outlined.LibraryBooks
    val FavoritesFilled = Icons.Filled.Favorite
    val FavoritesOutlined = Icons.Outlined.FavoriteBorder

    // TopAppBar
    val FilterListOutlined = Icons.Outlined.FilterList
    val PhoneFilled = Icons.Filled.Phone
    val MenuFilled = Icons.Filled.Menu
    val DeleteFilled = Icons.Filled.Delete
    val PersonFilled = Icons.Filled.Person
    val ShareFilled = Icons.Filled.Share
    val ArrowBackFilled = Icons.AutoMirrored.Filled.ArrowBack

    val LocationOff = Icons.Default.LocationOff
    val LocationDisabled = Icons.Outlined.LocationDisabled
    val MyLocation = Icons.Outlined.MyLocation
    val NavigationFilled = Icons.Filled.Navigation
    val CancelFilled = Icons.Filled.Cancel
    val Calculate = Icons.Filled.Calculate
    val ExpandMore = Icons.Filled.ExpandMore
    val Close = Icons.Filled.Close
    val Search = Icons.Outlined.Search
    val MoreVert = Icons.Filled.MoreVert
    val KeyboardArrowRight = Icons.AutoMirrored.Filled.KeyboardArrowRight
    val Exp = Icons.Outlined.BusinessCenter
    val Email = Icons.Filled.Email

    // Painter
    val LogoFullSolid = R.drawable.core_designsystem_logo_full_solid
    val LogoFullSolidPng = R.drawable.core_designsystem_logo_full_solid
    val Personal = R.drawable.core_designsystem_personal
    val Truck = R.drawable.core_designsystem_truck
    val Van = R.drawable.core_designsystem_van
    val Error = R.drawable.core_designsystem_logo_solid_mono_invert
    val StationClfs = R.drawable.core_designsystem_station_clfs
    val StationCng = R.drawable.core_designsystem_station_cng
    val StationNotWorking = R.drawable.core_designsystem_station_not_working
    val StationService = R.drawable.core_designsystem_station_service
}