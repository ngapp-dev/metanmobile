import SwiftUI
import MetanMobileComposeApp

@main
struct MetanMobileApp: App {
    init() {
        SharedKoinKt.doInitSharedKoin(nativeAdsBridge: MobileAdsBridge())
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
