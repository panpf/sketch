plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.test.http")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.internal.test)
            api(projects.sketchHttpKtor3)
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
    }
}