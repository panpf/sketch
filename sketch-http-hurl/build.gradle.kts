plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addMultiplatformTargets(arrayOf(MultiplatformTargets.Android, MultiplatformTargets.Desktop))

kotlin {
    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.http.hurl")

    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
            api(projects.sketchHttpCore)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
    }
}