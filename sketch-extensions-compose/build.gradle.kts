plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.compose")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchComposeCore)
            api(projects.sketchExtensionsCore)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
    }
}