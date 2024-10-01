plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.compose.resources")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchComposeResources)
            api(projects.sketchExtensionsCore)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtilsCompose)
        }
    }
}