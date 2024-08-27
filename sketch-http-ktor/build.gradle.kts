plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.http.ktor")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchHttpCore)
            api(libs.ktor.client.core)
        }
        androidMain.dependencies {
            api(libs.ktor.client.android)
        }
        desktopMain.dependencies {
            api(libs.ktor.client.java)
        }
        iosMain.dependencies {
            api(libs.ktor.client.ios)
        }
        jsMain.dependencies {
            api(libs.ktor.client.js)
        }
        wasmJsMain.dependencies {
            api(libs.ktor.client.core.wasm)
            api(libs.ktor.client.wasmJs)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
    }
}