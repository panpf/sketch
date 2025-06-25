plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.compose.koin")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchComposeCore)
            api(projects.sketchKoin)
            api(libs.koin.compose)
        }

        commonTest.dependencies {
            implementation(projects.internal.testKoin)
            implementation(projects.internal.testCompose)
        }
    }
}