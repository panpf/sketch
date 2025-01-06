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
    }
}