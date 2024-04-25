plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlinx-atomicfu")
}

addAllMultiplatformTargets()
androidLibrary(nameSpace = "com.github.panpf.sketch.test.utils")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.internal.testUtilsCore)
        }
    }
}