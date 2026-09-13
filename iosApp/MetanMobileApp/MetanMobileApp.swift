import SwiftUI
import MetanMobileComposeApp

@main
struct MetanMobileApp: App {
    init() {
        SharedKoinKt.doInitSharedKoin()
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
