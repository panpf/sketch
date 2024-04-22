plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchComposeCore)
            api(projects.sketchExtensionsCore)
            api(compose.foundation)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.compose")