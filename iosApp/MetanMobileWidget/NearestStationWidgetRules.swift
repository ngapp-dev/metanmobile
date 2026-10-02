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

// The widget's decisions, kept apart from the SwiftUI layout so tests can check them directly
// (MetanMobileWidgetTests). Same rules as the Android widget.

extension ThemeOption {
    /// Whether the widget draws its dark side, given the phone's own dark mode.
    func isDark(systemIsDark: Bool) -> Bool {
        switch self {
        case .system: systemIsDark
        case .light: false
        case .dark: true
        }
    }
}

extension TilesOption {
    /// What actually fits: a small widget shows one tile, "both" keeps the price there.
    func shown(isSmall: Bool) -> TilesOption {
        isSmall && self == .both ? .price : self
    }
}

/// The app for the widget as a whole. The station when it's the only thing shown: a small
/// widget has a single tap target. (Medium widgets also link the station tile on its own.)
func nearestStationWidgetURL(tiles: TilesOption, snapshot: NearestStationSnapshot?) -> URL? {
    if tiles == .distance, let station = snapshot?.station {
        return URL(string: station.url)
    }
    return URL(string: "metanmobile://")
}

extension NearestStationSnapshot {
    /// Decodes what the app wrote; nil for a missing or unreadable file.
    static func decode(_ data: Data) -> NearestStationSnapshot? {
        try? JSONDecoder().decode(NearestStationSnapshot.self, from: data)
    }

    /// True when there's something to show: the price, the station, or both.
    var hasContent: Bool { price != nil || station != nil }
}
