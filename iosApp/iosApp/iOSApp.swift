import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        AppKoinKt.doInitAppKoin(appDeclaration: { _ in })
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}