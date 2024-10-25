plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.http.ktor3")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchHttpKtor3Core)
        }
        androidMain.dependencies {
            api(libs.ktor3.client.android)
        }
        desktopMain.dependencies {
            api(libs.ktor3.client.java)
        }
        iosMain.dependencies {
            api(libs.ktor3.client.darwin)
        }
        jsMain.dependencies {
            api(libs.ktor3.client.js)
        }
        wasmJsMain.dependencies {
            api(libs.ktor3.client.wasmJs)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
    }
}