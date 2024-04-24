plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlinx-atomicfu")
}

addAllMultiplatformTargets()

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

androidLibrary(nameSpace = "com.github.panpf.sketch")