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

/// "Price and nearest station": the iOS counterpart of the Android NearestStationWidget. Small
/// shows one tile, medium shows the tiles chosen in its settings.
struct NearestStationWidget: Widget {
    /// Never change it: placed widgets are matched to the code by this kind.
    static let kind = "NearestStationWidget"

    var body: some WidgetConfiguration {
        AppIntentConfiguration(
            kind: Self.kind,
            intent: NearestStationWidgetIntent.self,
            provider: NearestStationProvider()
        ) { entry in
            NearestStationWidgetView(entry: entry)
        }
        .configurationDisplayName("Price and nearest station")
        .description("CNG price and the CNG station nearest to you")
        .supportedFamilies([.systemSmall, .systemMedium])
        // Same 12pt padding as the Android widget instead of the system's wider margins.
        .contentMarginsDisabled()
    }
}

struct NearestStationEntry: TimelineEntry {
    let date: Date
    /// Nil until the app has written its data (it hasn't run since the widget was added).
    let snapshot: NearestStationSnapshot?
    let configuration: NearestStationWidgetIntent
}

/// The data only changes when the app writes a new snapshot, and the app then asks WidgetKit to
/// reload (WidgetReloaderBridge) - so there's nothing to schedule here: `.never`.
struct NearestStationProvider: AppIntentTimelineProvider {
    func placeholder(in context: Context) -> NearestStationEntry {
        NearestStationEntry(date: .now, snapshot: .sample, configuration: NearestStationWidgetIntent())
    }

    func snapshot(for configuration: NearestStationWidgetIntent, in context: Context) async -> NearestStationEntry {
        // The widget gallery shows sample data rather than an empty widget.
        let snapshot = NearestStationSnapshot.load() ?? (context.isPreview ? .sample : nil)
        return NearestStationEntry(date: .now, snapshot: snapshot, configuration: configuration)
    }

    func timeline(for configuration: NearestStationWidgetIntent, in context: Context) async -> Timeline<NearestStationEntry> {
        let entry = NearestStationEntry(date: .now, snapshot: NearestStationSnapshot.load(), configuration: configuration)
        return Timeline(entries: [entry], policy: .never)
    }
}
