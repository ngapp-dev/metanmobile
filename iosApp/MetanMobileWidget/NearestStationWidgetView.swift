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
import WidgetKit

/// The layout, matching the Android widget (NearestStationWidgetContent.kt): a container with
/// one or two tiles - the CNG price and the nearest station.
struct NearestStationWidgetView: View {
    let entry: NearestStationEntry

    @Environment(\.widgetFamily) private var family
    @Environment(\.colorScheme) private var systemColorScheme
    /// Accented/vibrant on iOS 26's Tinted and Clear Home Screens: the system draws the glass
    /// and the colors, so the widget's own fills step back.
    @Environment(\.widgetRenderingMode) private var renderingMode

    private var configuration: NearestStationWidgetIntent { entry.configuration }

    private var isDark: Bool {
        switch configuration.theme {
        case .system: systemColorScheme == .dark
        case .light: false
        case .dark: true
        }
    }

    private var isFullColor: Bool { renderingMode == .fullColor }

    /// A small widget only has room for one tile; "both" keeps the price there.
    private var tiles: TilesOption {
        family == .systemSmall && configuration.tiles == .both ? .price : configuration.tiles
    }

    var body: some View {
        let palette = WidgetPalette(intent: configuration, isDark: isDark)
        content(palette)
            .padding(WidgetTokens.containerPadding)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .containerBackground(for: .widget) {
                isFullColor ? palette.container : Color.clear
            }
            .widgetURL(widgetURL)
    }

    @ViewBuilder
    private func content(_ palette: WidgetPalette) -> some View {
        if let snapshot = entry.snapshot, snapshot.price != nil || snapshot.station != nil {
            HStack(spacing: WidgetTokens.tileSpacing) {
                if tiles != .distance {
                    PriceTile(snapshot: snapshot, palette: palette, isFullColor: isFullColor)
                }
                if tiles != .price {
                    stationTile(snapshot: snapshot, palette: palette)
                }
                // One tile in a medium widget takes half of it, as on Android.
                if tiles != .both && family != .systemSmall {
                    Color.clear.frame(maxWidth: .infinity)
                }
            }
        } else {
            Tile(palette: palette, isFullColor: isFullColor) {
                Caption(
                    text: entry.snapshot?.texts.noData ?? String(localized: "Open the app to load the data"),
                    color: palette.onTile,
                    lineLimit: 3
                )
            }
        }
    }

    @ViewBuilder
    private func stationTile(snapshot: NearestStationSnapshot, palette: WidgetPalette) -> some View {
        let tile = StationTile(snapshot: snapshot, palette: palette, isFullColor: isFullColor)
        // Small widgets only support one tap target (widgetURL); medium ones can link the tile.
        if family != .systemSmall, let url = snapshot.station.flatMap({ URL(string: $0.url) }) {
            Link(destination: url) { tile }
        } else {
            tile
        }
    }

    /// The station when it's the only thing shown, otherwise just the app.
    private var widgetURL: URL? {
        if tiles == .distance, let station = entry.snapshot?.station {
            return URL(string: station.url)
        }
        return URL(string: "metanmobile://")
    }
}

private struct PriceTile: View {
    let snapshot: NearestStationSnapshot
    let palette: WidgetPalette
    let isFullColor: Bool

    var body: some View {
        Tile(palette: palette, isFullColor: isFullColor) {
            Value(text: snapshot.price?.value ?? "—", color: palette.onTile)
            Caption(text: snapshot.texts.cngPrice, color: palette.onTile)
            Caption(text: snapshot.texts.perCubicMeter, color: palette.onTile)
        }
    }
}

private struct StationTile: View {
    let snapshot: NearestStationSnapshot
    let palette: WidgetPalette
    let isFullColor: Bool

    var body: some View {
        Tile(palette: palette, isFullColor: isFullColor) {
            if let station = snapshot.station {
                HStack(spacing: 6) {
                    Value(text: station.distance, color: palette.onTile)
                    StatusDot(status: station.status, ring: palette.statusDotRing(for: station.status))
                }
                Caption(text: snapshot.texts.nearestStation, color: palette.onTile)
                Caption(text: station.address, color: palette.onTile)
            } else {
                // Location unknown: the app asks for the permission / finds the location on open.
                Caption(text: snapshot.texts.noLocation, color: palette.onTile, lineLimit: 4)
            }
        }
    }
}

private struct Tile<Content: View>: View {
    let palette: WidgetPalette
    let isFullColor: Bool
    @ViewBuilder let content: Content

    var body: some View {
        VStack(alignment: .leading, spacing: 2) { content }
            .padding(.horizontal, WidgetTokens.tilePaddingHorizontal)
            .padding(.vertical, WidgetTokens.tilePaddingVertical)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(
                RoundedRectangle(cornerRadius: WidgetTokens.tileCornerRadius, style: .continuous)
                    .fill(isFullColor ? AnyShapeStyle(palette.tile) : AnyShapeStyle(.fill.tertiary))
            )
    }
}

/// MMTypography.displayMedium
private struct Value: View {
    let text: String
    let color: Color

    var body: some View {
        Text(text)
            .font(.system(size: 21, weight: .bold))
            .foregroundStyle(color)
            .lineLimit(1)
            .minimumScaleFactor(0.7)
            .widgetAccentable()
    }
}

/// MMTypography.headlineMedium
private struct Caption: View {
    let text: String
    let color: Color
    var lineLimit = 1

    var body: some View {
        Text(text)
            .font(.system(size: 15, weight: .medium))
            .foregroundStyle(color)
            .lineLimit(lineLimit)
    }
}

/// Mirrors StationStatusView in core:ui; ringed when it would blend into the tile.
private struct StatusDot: View {
    let status: NearestStationSnapshot.Status
    let ring: Color?

    var body: some View {
        Circle()
            .fill(status.color)
            .frame(width: WidgetTokens.statusDotSize, height: WidgetTokens.statusDotSize)
            .padding(ring == nil ? 0 : WidgetTokens.statusDotRingWidth)
            .background(Circle().fill(ring ?? .clear))
    }
}

#Preview("Medium", as: .systemMedium) {
    NearestStationWidget()
} timeline: {
    NearestStationEntry(date: .now, snapshot: .sample, configuration: NearestStationWidgetIntent())
    NearestStationEntry(date: .now, snapshot: .sample, configuration: .blueTiles)
    NearestStationEntry(date: .now, snapshot: nil, configuration: NearestStationWidgetIntent())
}

#Preview("Small", as: .systemSmall) {
    NearestStationWidget()
} timeline: {
    NearestStationEntry(date: .now, snapshot: .sample, configuration: NearestStationWidgetIntent())
    NearestStationEntry(date: .now, snapshot: .sample, configuration: .distanceOnly)
}

private extension NearestStationWidgetIntent {
    static var blueTiles: NearestStationWidgetIntent {
        let intent = NearestStationWidgetIntent()
        intent.tileColor = .blue
        return intent
    }

    static var distanceOnly: NearestStationWidgetIntent {
        let intent = NearestStationWidgetIntent()
        intent.tiles = .distance
        return intent
    }
}
