plugins {
    id("com.android.library")
    id("kotlinx-atomicfu")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.singleton")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtilsCore)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testUtilsCore)
        }
    }
}