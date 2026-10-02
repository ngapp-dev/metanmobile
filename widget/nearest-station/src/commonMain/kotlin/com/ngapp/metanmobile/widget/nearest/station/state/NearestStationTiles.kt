/*
 * Copyright 2026 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
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

package com.ngapp.metanmobile.widget.nearest.station.state

/**
 * Which tiles the user wants. The widget's size can still drop one: a narrow widget only has
 * room for a single tile, so BOTH falls back to the price there (see [tilesFor]).
 */
enum class NearestStationTiles(val showsPrice: Boolean, val showsStation: Boolean) {
    PRICE(showsPrice = true, showsStation = false),
    DISTANCE(showsPrice = false, showsStation = true),
    BOTH(showsPrice = true, showsStation = true),
    ;

    /** What actually fits: a narrow widget shows one tile, the first of the chosen ones. */
    fun tilesFor(isWide: Boolean): NearestStationTiles =
        if (this == BOTH && !isWide) PRICE else this
}
