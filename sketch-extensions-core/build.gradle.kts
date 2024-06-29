plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.core")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testUtils)
        }
    }
}