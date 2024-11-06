plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets(
    *MultiplatformTargets.values()
        .filter { it != MultiplatformTargets.WasmJs }
        .toTypedArray()
)

androidLibrary(nameSpace = "com.github.panpf.sketch.http.ktor2.core")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
            api(projects.sketchHttpCore)
            api(libs.ktor2.client.core)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
            implementation(libs.ktor2.client.android)
        }
        desktopTest.dependencies {
            implementation(libs.ktor2.client.java)
        }
        iosTest.dependencies {
            implementation(libs.ktor2.client.darwin)
        }
        jsTest.dependencies {
            implementation(libs.ktor2.client.js)
        }
    }
}