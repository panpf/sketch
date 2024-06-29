plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.svg")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
        }
        androidMain.dependencies {
            api(libs.androidsvg)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testUtils)
        }
    }
}