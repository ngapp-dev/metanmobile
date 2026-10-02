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

/// The JSON contract with the Kotlin side (NearestStationWidgetSnapshot in
/// :widget:nearest-station, whose own test pins the same field names). The fixtures are exactly
/// what kotlinx.serialization writes - the first one was copied from a simulator's App Group.
struct NearestStationSnapshotTests {

    @Test func decodesWhatTheAppWritesWithAStation() throws {
        let json = """
        {"price":{"value":"1,16 BYN"},"station":{"code":"agnks_grodno2","distance":"2,4 km",\
        "address":"г. Гродно, Индурское шоссе, 15","status":"NOT_OPERATING",\
        "url":"metanmobile://ecogas-map/agnks_grodno2/"},"texts":{"cngPrice":"Цена метана",\
        "perCubicMeter":"За 1 куб. метр","nearestStation":"Ближайшая станция",\
        "noLocation":"Откройте приложение","noData":"Нет данных"}}
        """

        let snapshot = try #require(NearestStationSnapshot.decode(Data(json.utf8)))

        #expect(snapshot.price?.value == "1,16 BYN")
        let station = try #require(snapshot.station)
        #expect(station.code == "agnks_grodno2")
        #expect(station.distance == "2,4 km")
        #expect(station.status == .notOperating)
        #expect(station.url == "metanmobile://ecogas-map/agnks_grodno2/")
        #expect(snapshot.texts.nearestStation == "Ближайшая станция")
        #expect(snapshot.hasContent)
    }

    @Test func decodesTheNoLocationStateAsCapturedFromTheSimulator() throws {
        let json = """
        {"price":{"value":"1,16 BYN"},"station":null,"texts":{"cngPrice":"CNG price",\
        "perCubicMeter":"For 1 sq. m","nearestStation":"Nearest station",\
        "noLocation":"Open the app to find the nearest station","noData":"Open the app to load the data"}}
        """

        let snapshot = try #require(NearestStationSnapshot.decode(Data(json.utf8)))

        #expect(snapshot.station == nil)
        #expect(snapshot.price != nil)
        #expect(snapshot.hasContent)
    }

    @Test func nothingSyncedYetHasNoContent() throws {
        let json = """
        {"price":null,"station":null,"texts":{"cngPrice":"","perCubicMeter":"","nearestStation":"",\
        "noLocation":"","noData":"Open the app to load the data"}}
        """

        let snapshot = try #require(NearestStationSnapshot.decode(Data(json.utf8)))

        #expect(!snapshot.hasContent)
        #expect(snapshot.texts.noData == "Open the app to load the data")
    }

    @Test(arguments: [
        ("OPERATING", NearestStationSnapshot.Status.operating),
        ("NOT_OPERATING", .notOperating),
        ("UNKNOWN", .unknown),
    ])
    func decodesEveryStatusTheKotlinEnumHas(raw: String, expected: NearestStationSnapshot.Status) throws {
        let status = try JSONDecoder().decode(NearestStationSnapshot.Status.self, from: Data("\"\(raw)\"".utf8))
        #expect(status == expected)
    }

    @Test func aBrokenFileIsTreatedAsNoData() {
        #expect(NearestStationSnapshot.decode(Data("{".utf8)) == nil)
        #expect(NearestStationSnapshot.decode(Data()) == nil)
    }

    @Test func extraFieldsFromANewerAppVersionAreIgnored() throws {
        let json = """
        {"price":{"value":"1 BYN","currency":"BYN"},"station":null,"newField":true,\
        "texts":{"cngPrice":"","perCubicMeter":"","nearestStation":"","noLocation":"","noData":""}}
        """

        #expect(NearestStationSnapshot.decode(Data(json.utf8))?.price?.value == "1 BYN")
    }

    @Test func theSampleRoundTrips() throws {
        let data = try JSONEncoder().encode(NearestStationSnapshot.sample)
        #expect(NearestStationSnapshot.decode(data) == .sample)
    }
}
