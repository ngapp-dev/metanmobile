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

/// Shared with the app (both targets have this App Group in Signing & Capabilities). Must match
/// WIDGET_APP_GROUP in widget:core.
let widgetAppGroup = "group.com.ngapp.metanmobile"

/// What the app writes for this widget (NearestStationWidgetSnapshot in :widget:nearest-station).
/// The field names are a contract with that Kotlin class: change both together. Texts arrive
/// already translated into the app's language and the nearest station is already picked.
struct NearestStationSnapshot: Codable, Equatable {
    /// Nil until the app has synced its data at least once.
    let price: Price?
    /// Nil while the user's location is unknown.
    let station: Station?
    let texts: Texts

    struct Price: Codable, Equatable {
        let value: String
    }

    struct Station: Codable, Equatable {
        let code: String
        let distance: String
        let address: String
        let status: Status
        let url: String
    }

    enum Status: String, Codable {
        case operating = "OPERATING"
        case notOperating = "NOT_OPERATING"
        case unknown = "UNKNOWN"
    }

    struct Texts: Codable, Equatable {
        let cngPrice: String
        let perCubicMeter: String
        let nearestStation: String
        let noLocation: String
        let noData: String
    }

    /// Written by the app's NearestStationWidgetSnapshotWriter.
    static let fileName = "nearest-station.json"

    /// The app's latest data, or nil if it hasn't run (or synced) since the widget was added.
    static func load() -> NearestStationSnapshot? {
        guard
            let container = FileManager.default.containerURL(
                forSecurityApplicationGroupIdentifier: widgetAppGroup
            ),
            let data = try? Data(contentsOf: container.appendingPathComponent(fileName))
        else { return nil }
        return try? JSONDecoder().decode(NearestStationSnapshot.self, from: data)
    }

    /// For the widget gallery, placeholders and Xcode previews.
    static let sample = NearestStationSnapshot(
        price: Price(value: "1,16 BYN"),
        station: Station(
            code: "agnks_grodno2",
            distance: "2,4 km",
            address: "г. Гродно, Индурское шоссе, 15",
            status: .operating,
            url: "metanmobile://ecogas-map/agnks_grodno2/"
        ),
        texts: Texts(
            cngPrice: String(localized: "CNG price"),
            perCubicMeter: String(localized: "For 1 sq. m"),
            nearestStation: String(localized: "Nearest station"),
            noLocation: String(localized: "Open the app to find the nearest station"),
            noData: String(localized: "Open the app to load the data")
        )
    )
}
