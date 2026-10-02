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
import Testing

/// The palette rules ported from widget:core's WidgetTheme.kt (checked there by
/// WidgetPaletteTest) - both platforms' widgets must resolve colors the same way.
struct WidgetPaletteTests {

    @Test func theDefaultsLookLikeTheApp() {
        let light = WidgetPalette(intent: intent(), isDark: false)
        let dark = WidgetPalette(intent: intent(), isDark: true)

        #expect(light.container.rgba == WidgetTokens.white.rgba)
        #expect(light.tile.rgba == WidgetTokens.backgroundLight.rgba)
        #expect(light.onTile.rgba == WidgetTokens.black.rgba)
        #expect(dark.container.rgba == WidgetTokens.black.rgba)
        #expect(dark.tile.rgba == WidgetTokens.backgroundDark.rgba)
        #expect(dark.onTile.rgba == WidgetTokens.white.rgba)
    }

    @Test func anExplicitColorIgnoresTheTheme() {
        let palette = WidgetPalette(intent: intent { $0.backgroundColor = .white }, isDark: true)

        #expect(palette.container.rgba == WidgetTokens.white.rgba)
    }

    @Test(arguments: [
        (OpacityOption.none, 0.0),
        (.quarter, 0.25),
        (.half, 0.5),
        (.threeQuarters, 0.75),
        (.full, 1.0),
    ])
    func opacityBecomesTheFillsAlpha(option: OpacityOption, alpha: Double) {
        let palette = WidgetPalette(
            intent: intent { $0.tileColor = .blue; $0.tileOpacity = option },
            isDark: false
        )

        #expect(abs(palette.tile.rgba.alpha - alpha) < 0.01)
    }

    @Test(arguments: [
        (ColorOption.black, true),
        (.blue, true),
        (.white, false),
    ])
    func textIsWhiteOnDarkTilesAndDarkOnWhiteOnes(tileColor: ColorOption, whiteText: Bool) {
        let palette = WidgetPalette(intent: intent { $0.tileColor = tileColor }, isDark: false)

        let expected = whiteText ? WidgetTokens.white : WidgetTokens.black
        #expect(palette.onTile.rgba == expected.rgba)
    }

    @Test func seeThroughTilesTakeTheirTextColorFromWhatShowsThrough() {
        let palette = WidgetPalette(
            intent: intent {
                $0.backgroundColor = .white
                $0.tileColor = .black
                $0.tileOpacity = .quarter
            },
            isDark: false
        )

        #expect(palette.onTile.rgba == WidgetTokens.black.rgba)
    }

    @Test func aFullySeeThroughWidgetUsesWhiteTextForTheWallpaper() {
        let palette = WidgetPalette(
            intent: intent { $0.backgroundOpacity = .none; $0.tileOpacity = .none },
            isDark: false
        )

        #expect(palette.onTile.rgba == WidgetTokens.white.rgba)
    }

    @Test func theOperatingDotIsRingedOnBlueTilesOnly() {
        let blue = WidgetPalette(intent: intent { $0.tileColor = .blue }, isDark: false)
        let white = WidgetPalette(intent: intent { $0.tileColor = .white }, isDark: false)

        #expect(blue.statusDotRing(for: .operating)?.rgba == blue.onTile.rgba)
        #expect(blue.statusDotRing(for: .notOperating) == nil)
        #expect(white.statusDotRing(for: .operating) == nil)
    }

    @Test func statusColorsMatchTheApp() {
        #expect(NearestStationSnapshot.Status.operating.color.rgba == WidgetTokens.blue.rgba)
        #expect(NearestStationSnapshot.Status.notOperating.color.rgba == WidgetTokens.red.rgba)
        #expect(NearestStationSnapshot.Status.unknown.color.rgba == WidgetTokens.gray400.rgba)
    }

    private func intent(_ configure: (NearestStationWidgetIntent) -> Void = { _ in }) -> NearestStationWidgetIntent {
        let intent = NearestStationWidgetIntent()
        configure(intent)
        return intent
    }
}

/// Resolved components, so colors built in different ways compare by what they actually are.
struct RGBA: Equatable {
    let red: Double, green: Double, blue: Double, alpha: Double

    static func == (lhs: RGBA, rhs: RGBA) -> Bool {
        abs(lhs.red - rhs.red) < 0.005 && abs(lhs.green - rhs.green) < 0.005
            && abs(lhs.blue - rhs.blue) < 0.005 && abs(lhs.alpha - rhs.alpha) < 0.005
    }
}

extension Color {
    var rgba: RGBA {
        let resolved = resolve(in: EnvironmentValues())
        return RGBA(
            red: Double(resolved.red),
            green: Double(resolved.green),
            blue: Double(resolved.blue),
            alpha: Double(resolved.opacity)
        )
    }
}
