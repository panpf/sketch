plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addMultiplatformTargets(MultiplatformTargets.entries.toTypedArray())

kotlin {
    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.http.ktor3")

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
        androidDeviceTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
    }
}