plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.http")

kotlin {
    sourceSets {
        jvmCommonMain.dependencies {
            api(projects.sketchHttpHurl)
        }
        nonJvmCommonMain.dependencies {
            api(projects.sketchHttpKtor3)
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