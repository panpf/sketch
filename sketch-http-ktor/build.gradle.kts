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

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.ktor.client.android)
        }
        desktopTest.dependencies {
            implementation(libs.ktor.client.java)
        }
        iosTest.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jsTest.dependencies {
            implementation(libs.ktor.client.js)
        }
        wasmJsTest.dependencies {
            implementation(libs.ktor.client.wasmJs)
        }
    }
}