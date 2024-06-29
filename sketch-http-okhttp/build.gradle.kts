plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets(MultiplatformTargets.Android, MultiplatformTargets.Desktop)

androidLibrary(nameSpace = "com.github.panpf.sketch.http.okhttp")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchHttpCore)
            api(libs.okhttp3)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
    }
}