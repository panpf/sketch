plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addMultiplatformTargets(
    MultiplatformTargets.entries.toTypedArray()
        .filter { it != MultiplatformTargets.WasmJs }
        .toTypedArray()
)

kotlin {
    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.http.ktor2")

    sourceSets {
        commonMain.dependencies {
            api(projects.sketchHttpKtor2Core)
        }
        androidMain.dependencies {
            api(libs.ktor2.client.android)
        }
        desktopMain.dependencies {
            api(libs.ktor2.client.java)
        }
        iosMain.dependencies {
            api(libs.ktor2.client.darwin)
        }
        jsMain.dependencies {
            api(libs.ktor2.client.js)
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