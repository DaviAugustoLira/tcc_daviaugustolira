import SwiftUI
import ComposeApp
import FirebaseCore

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        return true
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    init() {
        AppKoinKt.doInitAppKoin(appDeclaration: { _ in })
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}