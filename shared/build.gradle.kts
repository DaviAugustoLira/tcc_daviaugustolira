import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.kotlinSerialization)
    // Bundled with the Kotlin Multiplatform Gradle plugin — no separate version needed.
    id("org.jetbrains.kotlin.native.cocoapods")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    // The GitLive Firebase wrapper only binds to the official Firebase iOS SDK, it doesn't
    // ship it — link the actual pods here so the iOS target can resolve them. Requires
    // `pod install` from iosApp/ (Podfile added there) and opening iosApp.xcworkspace, not
    // the .xcodeproj, in Xcode from now on.
    cocoapods {
        version = "1.0"
        summary = "Shared module (design system, navigation, cross-feature domain) for the indoor navigation app"
        homepage = "https://github.com/DaviAugustoLira/tcc_daviaugustolira"
        ios.deploymentTarget = "16.0"

        pod("FirebaseCore")
        pod("FirebaseAuth")
        pod("FirebaseFirestore")
        pod("FirebaseStorage")
        pod("FirebaseFunctions")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)

            // Kable — scanner BLE no Android (iOS usa CoreLocation, ver actual em iosMain).
            implementation(libs.kable.core)

            // BoM dos artefatos nativos com.google.firebase:* que o GitLive Firebase (acima)
            // referencia sem versão própria — `api` pra propagar pros consumidores (composeApp).
            // project.dependencies.platform(...): ver nota em composeApp/build.gradle.kts
            // (KT-58759 dentro de sourceSets.*.dependencies{}).
            api(project.dependencies.platform("com.google.firebase:firebase-bom:${libs.versions.firebaseBom.get()}"))
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Koin — DI (ver CLAUDE.md secao 0).
            api(libs.koin.core)

            // Coroutines/Flow — leituras de BLE, ver CLAUDE.md secao 6.
            api(libs.kotlinx.coroutines.core)

            // Firebase — GitLive KMP wrapper, usable directly from commonMain (Android + iOS).
            // Android still needs google-services.json processed by composeApp (see its
            // build.gradle.kts) to initialize FirebaseApp; iOS needs GoogleService-Info.plist
            // bundled into iosApp and the pods above linked.
            api(libs.firebase.auth)
            api(libs.firebase.firestore)
            api(libs.firebase.storage)
            api(libs.firebase.functions)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "br.edu.utfpr.pb.tcc_daviaugustolira.shared"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}
