plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
}

addMultiplatformTargets(MultiplatformTargets.entries.toTypedArray())

kotlin {
    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.test.singleton")

    sourceSets {
        commonMain.dependencies {
            api(projects.internal.test)
        }
        androidMain.dependencies {
            api(projects.internal.test)
        }
    }
}