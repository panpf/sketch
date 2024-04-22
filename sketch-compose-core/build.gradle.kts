plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
            api(compose.foundation)
            api(compose.ui)
            api(compose.components.resources)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.compose.core")