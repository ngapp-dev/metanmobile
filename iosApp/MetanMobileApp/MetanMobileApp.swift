import SwiftUI
import WidgetKit
import MetanMobileComposeApp

@main
struct MetanMobileApp: App {
    init() {
        SharedKoinKt.doInitSharedKoin(
            nativeAdsBridge: MobileAdsBridge(),
            widgetReloader: WidgetReloaderBridge()
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    MainViewControllerKt.handleDeepLink(url: url.absoluteString)
                }
        }
    }
}

/// Lets the shared Kotlin code redraw the home-screen widgets after it wrote their data into the
/// App Group: WidgetCenter is a Swift-only API (see widget:core's WidgetReloader).
final class WidgetReloaderBridge: WidgetReloader {
    func reloadAllWidgets() {
        WidgetCenter.shared.reloadAllTimelines()
    }
}
