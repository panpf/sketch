plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.http.core")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.stdlib)
            api(libs.kotlinx.coroutines.core)
            api(libs.okio)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
    }
}