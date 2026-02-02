import org.jetbrains.compose.resources.ResourcesExtension.ResourceClassGeneration

plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlinx.kover")
}

addMultiplatformTargets(MultiplatformTargets.entries.toTypedArray())

compose.resources {
    generateResClass = ResourceClassGeneration.Never
}

kotlin {
    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.compose.resources")

    sourceSets {
        commonMain.dependencies {
            api(projects.sketchComposeCore)
            api(libs.jetbrains.compose.components.resources)
        }

        commonTest.dependencies {
            implementation(projects.internal.testCompose)
            implementation(projects.internal.testSingleton)
        }
        androidDeviceTest.dependencies {
            implementation(projects.internal.testCompose)
            implementation(projects.internal.testSingleton)
        }
    }
}