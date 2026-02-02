plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addMultiplatformTargets(MultiplatformTargets.entries.toTypedArray())

kotlin {
    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.http.core")

    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testHttp)
            implementation(projects.internal.testSingleton)
        }
    }
}