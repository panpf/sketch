plugins {
    id("com.android.library")
    id("kotlinx-atomicfu")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.test.singleton")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.internal.test)
        }
        androidMain.dependencies {
            api(projects.internal.test)
        }
    }
}