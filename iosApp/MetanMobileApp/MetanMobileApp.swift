import FirebaseAnalytics
import FirebaseCore
import SwiftUI
import WidgetKit
import MetanMobileComposeApp

@main
struct MetanMobileApp: App {
    init() {
        // Reads GoogleService-Info.plist; must run before anything logs an event.
        FirebaseApp.configure()
        SharedKoinKt.doInitSharedKoin(
            nativeAdsBridge: MobileAdsBridge(),
            widgetReloader: WidgetReloaderBridge(),
            analyticsBridge: FirebaseAnalyticsBridge()
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

/// Sends the shared Kotlin code's analytics events (screen views, opened stations, sync, ...) to
/// Firebase Analytics, the same backend as Android: the Firebase iOS SDK has no Kotlin API (see
/// core:analytics' AnalyticsBridge).
final class FirebaseAnalyticsBridge: AnalyticsBridge {
    func logEvent(name: String, parameters: [String: String]) {
        Analytics.logEvent(name, parameters: parameters)
    }
}
