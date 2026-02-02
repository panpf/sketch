plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlinx.kover")
}

addMultiplatformTargets(MultiplatformTargets.entries.toTypedArray())

kotlin {
    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.compose.koin")

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