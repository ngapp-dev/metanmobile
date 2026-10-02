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

/// Every text the widget itself shows (its name, settings and the fallback messages) exists in
/// the app's three languages. The rest of its texts come translated from the app (the snapshot).
struct WidgetLocalizationTests {

    /// Keys of Localizable.xcstrings - the strings used in the widget's Swift code.
    static let keys = [
        "Widget appearance", "Show", "Theme", "Background color", "Background opacity",
        "Tile color", "Tile opacity", "Tiles", "Price", "Distance", "Both", "System", "Light",
        "Dark", "Color", "Auto (follows theme)", "White", "Black", "Blue", "Opacity",
        "Price and nearest station", "CNG price and the CNG station nearest to you",
        "CNG price", "For 1 sq. m", "Nearest station",
        "Open the app to find the nearest station", "Open the app to load the data",
    ]

    @Test(arguments: ["ru", "be"])
    func everyStringIsTranslated(language: String) throws {
        let table = try #require(Self.strings(for: language), "No \(language) strings in the bundle")
        let missing = Self.keys.filter { (table[$0] ?? "").isEmpty }

        #expect(missing.isEmpty, "Not translated into \(language): \(missing)")
    }

    @Test func translationsDifferFromTheEnglishText() throws {
        let ru = try #require(Self.strings(for: "ru"))

        #expect(ru["Price"] == "Цена")
        #expect(ru["Open the app to load the data"] == "Откройте приложение, чтобы загрузить данные")
    }

    private static func strings(for language: String) -> [String: String]? {
        let bundle = Bundle(for: BundleToken.self)
        guard let path = bundle.path(forResource: "Localizable", ofType: "strings", inDirectory: nil, forLocalization: language)
        else { return nil }
        return NSDictionary(contentsOfFile: path) as? [String: String]
    }
}

private final class BundleToken {}
