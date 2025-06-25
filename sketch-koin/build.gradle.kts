plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.koin")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
            api(libs.koin.core)
        }

        commonTest.dependencies {
            implementation(projects.internal.testKoin)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testKoin)
        }
    }
}