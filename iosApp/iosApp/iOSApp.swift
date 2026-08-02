import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        KoinInitKt.doInitKoin(appDeclaration: { _ in })
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}