plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchComposeCore)
            api(projects.sketchSingleton)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.compose")