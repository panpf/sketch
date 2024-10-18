plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets(MultiplatformTargets.Android, MultiplatformTargets.Desktop)

androidLibrary(nameSpace = "com.github.panpf.sketch.http.hurl")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
    }
}