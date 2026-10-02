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

import SwiftUI

/// The app's design tokens (core:designsystem Color.kt) and the widget palette rules, ported
/// from widget:core's WidgetTheme.kt so both platforms' widgets look and behave the same.
enum WidgetTokens {
    static let black = Color(hex: 0x1F1F1F)
    static let white = Color(hex: 0xFFFFFF)
    static let blue = Color(hex: 0x009CDE)
    static let red = Color(hex: 0xEB5757)
    static let gray400 = Color(hex: 0x919191)
    static let backgroundLight = Color(hex: 0xF5F2F5)
    static let backgroundDark = Color(hex: 0x24292E)

    static let containerPadding: CGFloat = 12
    static let tileSpacing: CGFloat = 8
    static let tilePaddingHorizontal: CGFloat = 12
    static let tilePaddingVertical: CGFloat = 8
    static let tileCornerRadius: CGFloat = 12
    static let statusDotSize: CGFloat = 6
    static let statusDotRingWidth: CGFloat = 2
}

/// Resolved colors for one widget, for the theme it's drawn in.
struct WidgetPalette {
    let container: Color
    let tile: Color
    let onTile: Color
    /// The swatch the tiles mostly look like, to keep the status dot visible on them.
    let tileLooksLike: ColorOption

    init(intent: NearestStationWidgetIntent, isDark: Bool) {
        func base(_ option: ColorOption, auto: Color) -> Color {
            switch option {
            case .auto: auto
            case .white: WidgetTokens.white
            case .black: WidgetTokens.black
            case .blue: WidgetTokens.blue
            }
        }
        // Matches the app: a white/dark container with tiles a shade off it, like cards.
        let autoContainer = isDark ? WidgetTokens.black : WidgetTokens.white
        let autoTile = isDark ? WidgetTokens.backgroundDark : WidgetTokens.backgroundLight

        container = base(intent.backgroundColor, auto: autoContainer)
            .opacity(intent.backgroundOpacity.value)
        tile = base(intent.tileColor, auto: autoTile).opacity(intent.tileOpacity.value)

        // Text sits on the tile, or on whatever shows through it: the background, then the
        // wallpaper - mostly dark behind see-through widgets, so white reads best there.
        func looksLike(_ color: ColorOption, _ opacity: OpacityOption) -> ColorOption? {
            opacity.value >= 0.5 ? color : nil
        }
        tileLooksLike = looksLike(intent.tileColor, intent.tileOpacity)
            ?? looksLike(intent.backgroundColor, intent.backgroundOpacity)
            ?? .black
        switch tileLooksLike {
        case .white: onTile = WidgetTokens.black
        case .black, .blue: onTile = WidgetTokens.white
        case .auto: onTile = isDark ? WidgetTokens.white : WidgetTokens.black
        }
    }

    /// A rim in the text color when the dot would disappear into a tile of its own color.
    func statusDotRing(for status: NearestStationSnapshot.Status) -> Color? {
        status == .operating && tileLooksLike == .blue ? onTile : nil
    }
}

extension NearestStationSnapshot.Status {
    /// Mirrors StationStatusView in core:ui.
    var color: Color {
        switch self {
        case .operating: WidgetTokens.blue
        case .notOperating: WidgetTokens.red
        case .unknown: WidgetTokens.gray400
        }
    }
}

extension Color {
    init(hex: UInt32) {
        self.init(
            red: Double((hex >> 16) & 0xFF) / 255,
            green: Double((hex >> 8) & 0xFF) / 255,
            blue: Double(hex & 0xFF) / 255
        )
    }
}
