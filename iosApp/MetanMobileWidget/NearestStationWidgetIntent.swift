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

import AppIntents
import WidgetKit

/// The widget's settings, edited through the system's "Edit Widget" sheet. Mirrors the Android
/// widget's settings screen (NearestStationWidgetSettings + WidgetAppearance), minus liquid
/// glass: on iOS 26 the system draws real glass itself (the Home Screen's Clear/Tinted styles).
struct NearestStationWidgetIntent: WidgetConfigurationIntent {
    static let title: LocalizedStringResource = "Widget appearance"

    @Parameter(title: "Show", default: .both)
    var tiles: TilesOption

    @Parameter(title: "Theme", default: .system)
    var theme: ThemeOption

    @Parameter(title: "Background color", default: .auto)
    var backgroundColor: ColorOption

    @Parameter(title: "Background opacity", default: .full)
    var backgroundOpacity: OpacityOption

    @Parameter(title: "Tile color", default: .auto)
    var tileColor: ColorOption

    @Parameter(title: "Tile opacity", default: .full)
    var tileOpacity: OpacityOption

    static var parameterSummary: some ParameterSummary {
        Summary {
            \.$tiles
            \.$theme
            \.$backgroundColor
            \.$backgroundOpacity
            \.$tileColor
            \.$tileOpacity
        }
    }
}

/// Which tiles to show. A small widget only has room for one: "both" keeps the price there.
enum TilesOption: String, AppEnum {
    case price, distance, both

    static let typeDisplayRepresentation: TypeDisplayRepresentation = "Tiles"
    static let caseDisplayRepresentations: [TilesOption: DisplayRepresentation] = [
        .price: "Price",
        .distance: "Distance",
        .both: "Both",
    ]
}

/// System follows the phone's dark mode; light and dark keep the widget that way regardless.
enum ThemeOption: String, AppEnum {
    case system, light, dark

    static let typeDisplayRepresentation: TypeDisplayRepresentation = "Theme"
    static let caseDisplayRepresentations: [ThemeOption: DisplayRepresentation] = [
        .system: "System",
        .light: "Light",
        .dark: "Dark",
    ]
}

/// The same swatches as on Android. Auto = light in the light theme, dark in the dark one.
enum ColorOption: String, AppEnum {
    case auto, white, black, blue

    static let typeDisplayRepresentation: TypeDisplayRepresentation = "Color"
    static let caseDisplayRepresentations: [ColorOption: DisplayRepresentation] = [
        .auto: "Auto (follows theme)",
        .white: "White",
        .black: "Black",
        .blue: "Blue",
    ]
}

/// The system sheet has no sliders, so opacity comes in steps.
enum OpacityOption: String, AppEnum {
    case none, quarter, half, threeQuarters, full

    static let typeDisplayRepresentation: TypeDisplayRepresentation = "Opacity"
    static let caseDisplayRepresentations: [OpacityOption: DisplayRepresentation] = [
        .none: "0%",
        .quarter: "25%",
        .half: "50%",
        .threeQuarters: "75%",
        .full: "100%",
    ]

    var value: Double {
        switch self {
        case .none: 0
        case .quarter: 0.25
        case .half: 0.5
        case .threeQuarters: 0.75
        case .full: 1
        }
    }
}
