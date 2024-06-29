plugins {
    id("com.android.library")
    id("kotlinx-atomicfu")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.test.utils")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.internal.testUtilsCore)
        }
        androidMain.dependencies {
            api(projects.internal.testUtilsCore)
        }
    }
}