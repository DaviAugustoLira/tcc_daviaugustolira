import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
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
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)

            // BoM do Firebase — ver :shared/build.gradle.kts. Declarado aqui também porque a
            // configuração androidTest do app não herda os artefatos `api` do :shared para
            // BOMs de plataforma da mesma forma que o classpath principal.
            // project.dependencies.platform(...), não platform(...) direto: dentro de
            // sourceSets.*.dependencies{} do KMP, KotlinDependencyHandler não tem os overloads
            // tipados de platform() do Gradle — só um legado depreciado (KT-58759) que virou
            // erro de compilação do script. Passar pelo DependencyHandler de verdade evita isso.
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:${libs.versions.firebaseBom.get()}"))
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

            implementation(project(":shared"))

            implementation(project(":navigation"))

            implementation(project(":feature:home"))
            implementation(project(":feature:search"))

            implementation(project(":feature:admin:login"))
            implementation(project(":feature:debug:beacons"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "br.edu.utfpr.pb.tcc_daviaugustolira"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "br.edu.utfpr.pb.tcc_daviaugustolira"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}
