//
//  Copyright 2026 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import Foundation
import Testing

/// The widget's decisions (NearestStationWidgetRules.swift), the same as the Android widget's.
struct NearestStationWidgetRulesTests {

    @Test func aSmallWidgetKeepsOneTile() {
        #expect(TilesOption.both.shown(isSmall: true) == .price)
        #expect(TilesOption.price.shown(isSmall: true) == .price)
        #expect(TilesOption.distance.shown(isSmall: true) == .distance)
    }

    @Test func aMediumWidgetShowsWhatWasChosen() {
        for tiles in [TilesOption.price, .distance, .both] {
            #expect(tiles.shown(isSmall: false) == tiles)
        }
    }

    @Test func onlyTheSystemThemeFollowsThePhone() {
        #expect(ThemeOption.system.isDark(systemIsDark: true))
        #expect(!ThemeOption.system.isDark(systemIsDark: false))
        #expect(!ThemeOption.light.isDark(systemIsDark: true))
        #expect(ThemeOption.dark.isDark(systemIsDark: false))
    }

    @Test func aDistanceOnlyWidgetOpensTheStation() {
        let url = nearestStationWidgetURL(tiles: .distance, snapshot: .sample)

        #expect(url?.absoluteString == "metanmobile://ecogas-map/agnks_grodno2/")
    }

    @Test func otherwiseTheWidgetOpensTheApp() {
        #expect(nearestStationWidgetURL(tiles: .both, snapshot: .sample)?.absoluteString == "metanmobile://")
        #expect(nearestStationWidgetURL(tiles: .price, snapshot: .sample)?.absoluteString == "metanmobile://")
        // No location yet: nothing to open but the app.
        #expect(nearestStationWidgetURL(tiles: .distance, snapshot: nil)?.absoluteString == "metanmobile://")
    }

    @Test func theAppHandlesTheStationLinkFormat() throws {
        // MetanMobileAppState.navigateToDeepLink strips "metanmobile://" and routes
        // "ecogas-map/{code}" to the station - the URL must keep exactly that shape.
        let url = try #require(URL(string: NearestStationSnapshot.sample.station!.url))

        #expect(url.scheme == "metanmobile")
        #expect(url.host() == "ecogas-map")
        #expect(url.pathComponents.filter { $0 != "/" } == ["agnks_grodno2"])
    }
}
