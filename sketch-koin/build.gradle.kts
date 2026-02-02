plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
    id("org.jetbrains.kotlinx.kover")
}

addMultiplatformTargets(MultiplatformTargets.entries.toTypedArray())

kotlin {
    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.koin")

    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
            api(libs.koin.core)
        }

        commonTest.dependencies {
            implementation(projects.internal.testKoin)
        }
        androidDeviceTest.dependencies {
            implementation(projects.internal.testKoin)
        }
    }
}